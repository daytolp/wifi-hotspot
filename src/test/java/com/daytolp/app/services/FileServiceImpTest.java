package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.models.AccessPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceImpTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @InjectMocks
    private FileServiceImp fileServiceImp;

    private AccessPointProcessResponse accessPointProcessResponse;
    @BeforeEach
    void setUp() {
        accessPointProcessResponse = AccessPointProcessResponse.builder()
                .status("OK")
                .message("Archivo procesado exitosamente")
                .build();
    }

    @Test
    void processFileTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "access_points.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "MEX-AIM-AER-AICMT1-M-GW001\tAeropuerto\t19.432707\t-99.086743\tVenustiano Carranza\n".getBytes()
        );

        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.setStatus(BatchStatus.COMPLETED);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        AccessPointProcessResponse accessPointProcess = fileServiceImp.processFile(file);

        assertNotNull(accessPointProcess);
        assertEquals(accessPointProcessResponse.getMessage(), accessPointProcess.getMessage());
        assertEquals(accessPointProcessResponse.getStatus(), accessPointProcess.getStatus());

    }

    @Test
    void processFileExceptionTest() throws Exception {
        String messageExpected = "El archivo debe ser de tipo .xlsx";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "access_points.pdf",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "MEX-AIM-AER-AICMT1-M-GW001\tAeropuerto\t19.432707\t-99.086743\tVenustiano Carranza\n".getBytes()
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileServiceImp.processFile(file);
        });
        String message = exception.getMessage();
        assertNotNull(message);
        assertEquals(messageExpected, message);

    }
}
