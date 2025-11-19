package com.daytolp.app.config;

import com.daytolp.app.batch.steps.DeleteFileStep;
import com.daytolp.app.batch.steps.ReaderStep;
import com.daytolp.app.batch.steps.WriterStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @JobScope
    public ReaderStep accessPointReaderStep(){
        return new ReaderStep();
    }

    @Bean
    @JobScope
    public WriterStep accessPointWriterStep(){
        return new WriterStep();
    }

    @Bean
    @JobScope
    public DeleteFileStep deleteProcessorStep(){
        return new DeleteFileStep();
    }

    @Bean
    public Step readFileStep() {
        return stepBuilderFactory.get("accessPointReaderStep")
                .tasklet(accessPointReaderStep()).build();
    }

    @Bean
    public Step writerDataStep() {
        return stepBuilderFactory.get("accessPointWriterStep")
                .tasklet(accessPointWriterStep()).build();
    }

    @Bean
    public Step deleteFileStep() {
        return stepBuilderFactory.get("deleteProcessorStep")
                .tasklet(deleteProcessorStep()).build();
    }

    @Bean
    public Job readExcelJob() {
        return jobBuilderFactory
                .get("readExcelJob")
                .start(readFileStep())
                .next(writerDataStep())
                .next(deleteFileStep()).build();
    }

}
