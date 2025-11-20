package com.daytolp.app.services;

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

/**
 * Implementación del servicio de procesamiento de archivos Excel con puntos de acceso WiFi.
 */
@Slf4j
@Service
public class FileServiceImp implements FileService {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    /**
     * Procesa un archivo Excel con puntos de acceso WiFi mediante un job de Spring Batch.
     *
     * @param multipartFile Archivo Excel (.xlsx) cargado desde el cliente HTTP.
     *                      Debe contener puntos de acceso WiFi en el formato especificado.
     *                      No debe ser null ni estar vacío.
     * @return Objeto AccessPointProcessResponse que contiene listas de IDs guardados y omitidos.
     */
    @Override
    public AccessPointProcessResponse processFile(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        if (name == null || !name.endsWith(".xlsx")) {
            throw new IllegalArgumentException("El archivo debe ser de tipo .xlsx");
        }

        try {
            String dateStr = String.valueOf(new Date().getTime());
            Path temp = Files.createTempFile("access_points-" + dateStr, ".xlsx");
            multipartFile.transferTo(temp);

            log.info("-------------> INICIO DEL PROCESO BATCH <------------");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("fecha", new Date())
                    .addString("routeFile", temp.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("-------------> FIN DEL PROCESO BATCH <------------");

            return AccessPointProcessResponse.builder()
                    .status("OK")
                    .message("Archivo procesado exitosamente")
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
