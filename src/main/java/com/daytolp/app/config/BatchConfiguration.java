package com.daytolp.app.config;

import com.daytolp.app.batch.steps.ExcelAccessPointItemReader;
import com.daytolp.app.constanst.Constants;
import com.daytolp.app.models.AccessPoint;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import javax.persistence.EntityManagerFactory;


@EnableBatchProcessing
@EnableTransactionManagement
@Configuration
public class BatchConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job readExcelJob(Step excelToDbStep) {
        return jobBuilderFactory.get("readExcelJob")
                .incrementer(new RunIdIncrementer())
                .start(excelToDbStep)
                .build();
    }

    @Bean
    public Step excelToDbStep(ItemStreamReader<AccessPoint> accessPointReader,
                              ItemProcessor<AccessPoint, AccessPoint> accessPointProcessor,
                              JpaItemWriter<AccessPoint> accessPointWriter) {

        return stepBuilderFactory.get("excelToDbStep")
                .<AccessPoint, AccessPoint>chunk(Constants.CHUNK_SIZE)
                .reader(accessPointReader(null))
                .processor(accessPointProcessor)
                .writer(accessPointWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemStreamReader<AccessPoint> accessPointReader(
            @Value("#{jobParameters['routeFile']}") String routeFile) {
        return new ExcelAccessPointItemReader(routeFile);
    }


    @Bean
    public ItemProcessor<AccessPoint, AccessPoint> accessPointProcessor() {
        return item -> item;
    }

    @Bean
    public JpaItemWriter<AccessPoint> accessPointWriter(EntityManagerFactory emf) {
        JpaItemWriter<AccessPoint> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

}
