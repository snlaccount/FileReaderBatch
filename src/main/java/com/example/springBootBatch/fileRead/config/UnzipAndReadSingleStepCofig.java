package com.example.springBootBatch.fileRead.config;

import com.example.springBootBatch.fileRead.writer.ConsoleItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class UnzipAndReadSingleStepCofig {

    Logger logger = LoggerFactory.getLogger(UnzipAndReadSingleStepCofig.class);

    @Value( "${xml.file.input.path}")
    private String filepath;

    @Value("${zip.file.input.path}")
    private Resource[] inputResources;

    //@Value("D:/Test/Tempfiles/VehicleInfo*.csv")
    @Value("D:\\Test\\Tempfiles\\VehicleInfo1.csv")
    private Resource[] multiResource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobLauncher jobLauncher;


    @Bean
    @StepScope
    public MultiResourceItemReader<String> zipMultiResourceItemReader() throws MalformedURLException {
        //unzipFiles();
        MultiResourceItemReader<String> resourceItemReader = new MultiResourceItemReader<>();
        /*PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
       try {
           resourceItemReader.setResources(resolver.getResources("file:" +multiResource));
       } catch (IOException e) {
           e.printStackTrace();
       }*/
        // resourceItemReader.setResources(new Resource[] {new UrlResource("D:\\Test\\Tempfiles\\VehicleInfo1.csv")});

        Resource[] inputResources = null;
        FileSystemXmlApplicationContext patternResolver = new FileSystemXmlApplicationContext();
        try {
            //inputResources = patternResolver.getResources("D:\\Test\\Tempfiles\\Extracted\\*.xml");
            inputResources = patternResolver.getResources("D:\\Test\\Tempfiles\\\\ZipFolder\\*.zip");

        } catch (IOException e) {
            e.printStackTrace();
        }
        resourceItemReader.setResources(inputResources);
        System.out.println("In MultiResourceItemReader");
        resourceItemReader.setDelegate(zipReader());

        return resourceItemReader;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> zipReader()
    {
        FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        System.out.println("In FlatFileItemReader");
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }

    @Bean
    @StepScope
    public ConsoleItemWriter<String> writer()
    {
        return new ConsoleItemWriter<String>();
    }


    @Bean
    public Step step1() throws IOException {
        return stepBuilderFactory.get("step1").<String, String>chunk(5)
                .reader(zipMultiResourceItemReader())
                .writer(writer())
                .build();
    }
    @Bean
    public Job readZipFilesJob() throws IOException {
        return jobBuilderFactory
                .get("readZipFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Scheduled(cron = "${spring.batch.job.cron.expression}")
    public void xmlToDBJobSchedule() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder().addDate("launchDate", new Date())
                .toJobParameters();
        jobLauncher.run(readZipFilesJob(), jobParameters);
    }

}
