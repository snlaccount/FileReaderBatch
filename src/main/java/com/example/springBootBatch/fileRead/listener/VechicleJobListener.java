package com.example.springBootBatch.fileRead.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class VechicleJobListener extends JobExecutionListenerSupport {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.err.println("==========BEFORE JOB EXECUTION=====");
	}
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.err.println("==========AFTER JOB EXECUTION===Execution.getId()=="+jobExecution.getId()+"===jobExecution.getStatus()==="+jobExecution.getStatus());
	}

}
