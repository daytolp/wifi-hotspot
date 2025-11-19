package com.daytolp.app.batch.steps;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.models.AccessPoint;
import com.daytolp.app.services.AccessPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class WriterStep implements Tasklet {

    @Autowired
    private AccessPointService accessPointService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("------------------- Inicio del paso de escritura de los datos ------------------");
        ExecutionContext jobContext = chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        @SuppressWarnings("unchecked")
        List<AccessPoint> accessPoints = (List<AccessPoint>)
                jobContext.get("accessPoints");

        AccessPointProcessResponse result = accessPointService.processAccessPoints(accessPoints);

//        jobContext.put("savedIds", result.getIdsSaves());
//        jobContext.put("skippedIds", result.getIdsSkips());

        log.info("------------------- Fin del paso de escritura de los datos <------------------");
        return RepeatStatus.FINISHED;
    }
}
