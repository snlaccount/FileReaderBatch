package com.example.springBootBatch.fileRead.config;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipSample {

    public static void main (String[] args){
        File file = new File("D://Test//Tempfiles//SampleFiles.zip");

        //ZipFile zipFile = new ZipFile("C:/test.zip");
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile("D:/Test/Tempfiles/SampleFiles.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            System.out.printf("File: %s Size %d Modified on %TD %n", entry.getName(), entry.getSize(), new Date(entry.getTime()));
            try {
                InputStream stream = zipFile.getInputStream(entry);
                System.out.println(stream.toString());
               // System.out.println(stream.readAllBytes(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                System.out.println(br);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
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

        /*System.out.println("another way");
        String fileName =  "D:/Test/Tempfiles/SampleFiles.zip";
        try (FileInputStream fis = new FileInputStream(fileName);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {

            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {

                System.out.format("File: %s Size: %d Last Modified %s %n",
                        ze.getName(), ze.getSize(),
                        LocalDate.now());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    }//main




}
