package com.example.springBootBatch.fileRead.config;

import com.example.springBootBatch.fileRead.writer.ConsoleItemWriter;
import org.apache.commons.io.IOUtils;
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
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/*@Configuration
@EnableBatchProcessing
@EnableScheduling*/
public class UnzipFileUsingTaskletConfig {


    Logger logger = LoggerFactory.getLogger(UnzipFileConfig.class);


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


    public ByteArrayOutputStream readFromZip(){
        File file = new File("D://Test//Tempfiles//SampleFiles.zip");

        //ZipFile zipFile = new ZipFile("C:/test.zip");
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile("D:/Test/Tempfiles/SampleFiles.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            System.out.printf("File: %s Size %d Modified on %TD %n", entry.getName(), entry.getSize(), new Date(entry.getTime()));
            try {
                InputStream stream = zipFile.getInputStream(entry);
                System.out.println(stream.toString());
                // System.out.println(stream.readAllBytes(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                System.out.println(br);
                result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = stream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                System.out.println(result.toString(StandardCharsets.UTF_8.name()));
                System.out.println(IOUtils.toString(stream, "UTF-8"));
                //System.out.println(CharStreams.toString(new InputStreamReader(fis, "UTF-8")));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void readFromZipMultiResource() {

    }



    public void unzipFiles(){
        String fileZip = "D:\\Test\\Tempfiles\\SampleFiles.zip";
        File destDir = new File("D:\\Test\\Tempfiles\\Extracted");
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }




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
            inputResources = patternResolver.getResources("D:\\Test\\Tempfiles\\Extracted\\*.xml");
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


    /*@Bean
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
    }*/
    @Bean
    public UnZipTask unZipTask(){
        return new UnZipTask();
    }

    @Bean
    public Step unzipFileStep(){
        return stepBuilderFactory.get("unzipFileStep")
                .tasklet(unZipTask())
                .build();
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
                .start(unzipFileStep())
                .next(step1())
                .build();
    }

    @Scheduled(cron = "${spring.batch.job.cron.expression}")
    public void xmlToDBJobSchedule() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder().addDate("launchDate", new Date())
                .toJobParameters();
        jobLauncher.run(readZipFilesJob(), jobParameters);
    }

}
