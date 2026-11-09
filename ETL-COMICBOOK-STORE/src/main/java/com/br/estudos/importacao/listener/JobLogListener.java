package com.br.estudos.importacao.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobLogListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        MDC.put("jobExecutionId", String.valueOf(jobExecution.getId()));
        log.info("Job {} iniciado | parametro= {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job {} finalizado | status={} inicio={} fim={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime());

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            for (Throwable erro : jobExecution.getAllFailureExceptions()) {
                log.error("Falha no job", erro);
            }
        }
        MDC.remove("jobExecutionId");
    }
}
