package com.example.springBootBatch.fileRead.config;

import com.example.springBootBatch.fileRead.model.Vehicle;
import com.example.springBootBatch.fileRead.model.VehicleInformation;
import com.example.springBootBatch.fileRead.processor.VehicleItemProcessor;
import com.example.springBootBatch.fileRead.util.Constants;
import com.example.springBootBatch.fileRead.util.VehiclePreparedSmtSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * Spring batch config file.
 * All job and step level details are mentioend here
 * to conect to h2 DB : http://localhost:8081/console/login.do
 * to invoke job: http://localhost:8081/dummy/invokejob
 * Importante tables to check:
 *     BATCH_JOB_INSTANCE    gives instance id with job key
 *     BATCH_JOB_EXECUTION   detailed status of job
 *     BATCH_STEP_EXECUTION  step detail with status and error meessage
 *
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class JobConfig {

    Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value( "${xml.file.input.path}")
    private String filepath;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobLauncher jobLauncher;


    @Bean
    public ItemReader<Vehicle> xmlFileItemReader(){
        StaxEventItemReader<Vehicle> xmlFileReader = new StaxEventItemReader<>();
        MultiResourceItemReader<Vehicle> multiResourceItemReader = new MultiResourceItemReader<>();
        //xmlFileReader.setResource(new ClassPathResource(filepath));
       /* Resource[] resources = null;
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            resources = patternResolver.getResources("file:" + "D:\\Test\\*.xml");
            for (Resource res : resources ){
                xmlFileReader.setResource(res);
            }
            multiResourceItemReader.setResources(resources);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
       /* try{
            xmlFileReader.setResource(new UrlResource("file:" + "D:\\Test\\*.xml"));

        }catch (MalformedURLException e) {
            e.printStackTrace();
            e.getMessage();
        }*/
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        xmlFileReader.setResource(resolver.getResource("file:" + "D:\\Test\\VehicleInfo.xml"));

        //xmlFileReader.setFragmentRootElementName("Vehicle");
        xmlFileReader.setFragmentRootElementNames(new String[] {"Vehicle", "VehicleInformation" });

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Vehicle.class, VehicleInformation.class);

        xmlFileReader.setUnmarshaller(marshaller);
        return xmlFileReader;

    }

    @Bean
    public ItemProcessor<Vehicle, Vehicle> xmlFileItemProcessor() {
        return new VehicleItemProcessor();
    }

    @Bean
    public ItemWriter<Vehicle> xmlFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<Vehicle> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setJdbcTemplate(jdbcTemplate);

        databaseItemWriter.setSql(Constants.INSERT_QUERY_OLD);

        ItemPreparedStatementSetter<Vehicle> vehiclePreparedSmtSetter = new VehiclePreparedSmtSetter();
        databaseItemWriter.setItemPreparedStatementSetter(vehiclePreparedSmtSetter);

        return databaseItemWriter;
    }

    @Bean
    public Step xmlFileToDatabaseStep(ItemReader<Vehicle> xmlFileItemReader,
                               ItemProcessor<Vehicle, Vehicle> xmlFileItemProcessor,
                               ItemWriter<Vehicle> xmlFileDatabaseItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("xmlFileToDatabaseStep")
                .<Vehicle, Vehicle>chunk(1)
                .reader(xmlFileItemReader)
                .processor(xmlFileItemProcessor)
                .writer(xmlFileDatabaseItemWriter)
                .build();
    }

    @Bean
    public Job xmlFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("xmlFileToDatabaseStep") Step xmlStudentStep) {
        return jobBuilderFactory.get("xmlFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(xmlStudentStep)
                .end()
                .build();
    }




    /*@Scheduled(cron = "${spring.batch.job.cron.expression}")
    public void xmlToDBJobSchedule() {
            JobParameters jobParameters = new JobParametersBuilder().addDate("launchDate", new Date())
                    .toJobParameters();
            jobLauncher.run(xmlFileToDatabaseJob(), jobParameters);
    }*/


}
