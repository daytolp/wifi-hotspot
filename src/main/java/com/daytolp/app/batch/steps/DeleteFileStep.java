package com.daytolp.app.batch.steps;

import com.daytolp.app.constanst.Constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

@Slf4j
public class DeleteFileStep implements Tasklet {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String routeFile = chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getJobParameters()
                .getString(Constants.ROUTE_FILE);

        if (routeFile == null) {
            throw new IllegalStateException("No se encontró 'routeFile' en el ExecutionContext");
        }

        File file = new File(routeFile);
        if (file.exists()) {
            boolean deleted = file.delete();
            log.info("Archivo con ruta: {} eliminado: {}", file.getAbsolutePath(), deleted);
        }
        return RepeatStatus.FINISHED;
    }
}
