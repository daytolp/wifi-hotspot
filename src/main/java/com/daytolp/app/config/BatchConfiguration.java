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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


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
                              JdbcBatchItemWriter<AccessPoint> accessPointWriter) {

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
     * Escritor JPA que persiste los puntos de acceso en la base de datos si algun registro ya existe lo actualiza.
     *
     * @param dataSource fuente de datos para la conexión a la base de datos
     * @return escritor JDBC listo para usar en el Step
     */
    @Bean
    public JdbcBatchItemWriter<AccessPoint> accessPointWriter(DataSource dataSource) {
        String sql = """
            INSERT INTO access_point (id, programa, latitud, longitud, alcaldia)
            VALUES (:id, :program, :latitude, :longitude, :municipality)
            ON CONFLICT (id)
            DO UPDATE SET
                programa = EXCLUDED.programa,
                latitud = EXCLUDED.latitud,
                longitud = EXCLUDED.longitud,
                alcaldia = EXCLUDED.alcaldia
            """;

        return new JdbcBatchItemWriterBuilder<AccessPoint>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("id", item.getId());
                    params.addValue("program", item.getProgram());
                    params.addValue("latitude", item.getLatitude());
                    params.addValue("longitude", item.getLongitude());
                    params.addValue("municipality", item.getMunicipality());
                    return params;
                })
                .build();
    }

}
