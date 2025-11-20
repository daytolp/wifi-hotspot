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


/**
 * Configuración central de Spring Batch para el proceso de importación de puntos de acceso desde Excel.
 *
 */
@EnableBatchProcessing
@EnableTransactionManagement
@Configuration
public class BatchConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Define el Job principal que ejecuta el Step de lectura y persistencia.
     *
     * @param excelToDbStep Step que realiza la carga del Excel hacia la base de datos
     * @return instancia del Job configurado
     */
    @Bean
    public Job readExcelJob(Step excelToDbStep) {
        return jobBuilderFactory.get("readExcelJob")
                .incrementer(new RunIdIncrementer())
                .start(excelToDbStep)
                .build();
    }

    /**
     * Define el Step que orquesta la lectura del Excel, el procesamiento y la escritura en la base de datos.
     *
     * @param accessPointReader lector de puntos de acceso inyectado con alcance de Step
     * @param accessPointProcessor procesador identidad
     * @param accessPointWriter escritor JPA para persistir entidades
     * @return Step configurado
     */
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

    /**
     * Crea el lector de puntos de acceso con alcance de Step.
     *
     * @param routeFile ruta del archivo Excel proporcionada como parámetro del Job
     * @return lector que convierte filas de Excel en objetos AccessPoint
     */
    @Bean
    @StepScope
    public ItemStreamReader<AccessPoint> accessPointReader(
            @Value("#{jobParameters['routeFile']}") String routeFile) {
        return new ExcelAccessPointItemReader(routeFile);
    }

    /**
     * Procesador de puntos de acceso.
     *
     * @return procesador que devuelve el mismo AccessPoint recibido
     */
    @Bean
    public ItemProcessor<AccessPoint, AccessPoint> accessPointProcessor() {
        return item -> item;
    }

    /**
     * Escritor JPA que persiste los puntos de acceso en la base de datos.
     *
     * @param emf fábrica de EntityManager configurada por Spring
     * @return escritor JPA listo para usar en el Step
     */
    @Bean
    public JpaItemWriter<AccessPoint> accessPointWriter(EntityManagerFactory emf) {
        JpaItemWriter<AccessPoint> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

}
