package com.example.springBootBatch.fileRead.writer;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ConsoleItemWriter<T> implements ItemWriter<T> {
    @Override
    public void write(List<? extends T> items) throws Exception {
        //System.out.println("in writer");
        for (T item : items) {
            System.out.println(item);
        }
    }
}
