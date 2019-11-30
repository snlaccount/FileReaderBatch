package com.example.springBootBatch.fileRead.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipMultiResourceItemReader<T> extends MultiResourceItemReader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ZipMultiResourceItemReader.class);

    private Resource[] resources;
    private ZipFile[] zipFiles;


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // really used with archives?
        if (resources != null) {
            // overwrite the comparator to use description
            // instead of filename, the itemStream can only
            // have that description
            this.setComparator(new Comparator<Resource>() {

                /** Compares resource descriptions. */
                @Override
                public int compare(Resource r1, Resource r2) {
                    return r1.getDescription().compareTo(r2.getDescription());
                }
            });
            // get the inputStreams from all files inside the archives
            zipFiles = new ZipFile[resources.length];
            List<Resource> extractedResources = new ArrayList<Resource>();
            try {
                for (int i = 0; i < resources.length; i++) {
                    // find files inside the current zip resource
                    zipFiles[i] = new ZipFile(resources[i].getFile());
                    extractFiles(zipFiles[i], extractedResources);
                }
            } catch (Exception ex) {
                throw new ItemStreamException(ex);
            }
            LOG.info("before setting extracted resrouces:");
            // propagate extracted resources
            this.setResources(extractedResources.toArray(new Resource[extractedResources.size()]));
            LOG.info("after setting extracted resrouces:");
        }
        LOG.info("before opening execution context");
        super.open(executionContext);
        LOG.info("after opening exectuion context");
    }


    @Override
    public void close() throws ItemStreamException {
        super.close();
        // try to close all used zipfiles
        if (zipFiles != null) {
            for (int i = 0; i < zipFiles.length; i++) {
                try {
                    zipFiles[i].close();
                } catch (IOException ex) {
                    throw new ItemStreamException(ex);
                }
            }
        }
    }

    private static void extractFiles(final ZipFile currentZipFile, final List<Resource> extractedResources) throws IOException {
        Enumeration<? extends ZipEntry> zipEntryEnum = currentZipFile.entries();
        while (zipEntryEnum.hasMoreElements()) {
            ZipEntry zipEntry = zipEntryEnum.nextElement();
            LOG.info("extracting:" + zipEntry.getName());
            // traverse directories
            if (!zipEntry.isDirectory()) {
                // add inputStream
                extractedResources.add(
                        new InputStreamResource(
                                currentZipFile.getInputStream(zipEntry),
                                zipEntry.getName()));
                LOG.info("using extracted file:" + zipEntry.getName());
            }
        }
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }
}
