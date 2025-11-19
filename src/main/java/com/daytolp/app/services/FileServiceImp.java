package com.daytolp.app.services;

import com.daytolp.app.constanst.Constants;
import com.daytolp.app.dtos.AccessPointProcessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FileServiceImp implements FileService {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Override
    public AccessPointProcessResponse processFile(MultipartFile multipartFile) {
        try {
            Path temp = Files.createTempFile("access_points-", ".xlsx");
            multipartFile.transferTo(temp);
            log.info("-------------> INICIO DEL PROCESO BATCH <------------");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("fecha", new Date())
                    .addString("routeFile", temp.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            JobExecution execution = jobLauncher.run(job, jobParameters);
            ExecutionContext ctx = execution.getExecutionContext();

            @SuppressWarnings("unchecked")
            List<String> savedIds = (List<String>) ctx.get("savedIds");
            @SuppressWarnings("unchecked")
            List<String> skippedIds = (List<String>) ctx.get("skippedIds");


            return AccessPointProcessResponse.builder()
                    .idsSaves(savedIds)
                    .idsSkips(skippedIds)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error I/O al procesar el archivo", e);
        }  catch (JobExecutionAlreadyRunningException |
            JobRestartException |
            JobInstanceAlreadyCompleteException |
            JobParametersInvalidException e) {
                throw new RuntimeException("Error al ejecutar el job batch", e);
        }
    }
}
