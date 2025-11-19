package com.daytolp.app.batch.steps;

import com.daytolp.app.constanst.Constants;
import com.daytolp.app.excel.ExcelReader;
import com.daytolp.app.models.AccessPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class ReaderStep implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(ReaderStep.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ExcelReader excelReader;

   @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--------- INICIANDO DEL PASO DE LECTURA -----------");

//        File file = resourceLoader.getResource( Constants.CLASSPATH + File.separator + Constants.FILE_NAME).getFile();
//        FileInputStream fileInputStream = new FileInputStream(file);

       ExecutionContext jobContext = chunkContext
               .getStepContext()
               .getStepExecution()
               .getJobExecution()
               .getExecutionContext();

        String routeFile = jobContext.getString("routeFile");
        if (routeFile == null) {
           throw new IllegalStateException("No se encontró 'routeFile' en el ExecutionContext");
        }

       List<AccessPoint> accessPoints;
       try (FileInputStream fis = new FileInputStream(routeFile)) {
           accessPoints = excelReader.readExcelFile(fis);
       }

        jobContext.put("accessPoints", accessPoints);
        log.info("--------- FINALIZANDO DEL PASO DE LECTURA -----------");
        return RepeatStatus.FINISHED;
    }
}
