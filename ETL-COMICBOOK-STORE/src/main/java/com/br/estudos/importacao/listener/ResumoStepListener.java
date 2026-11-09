package com.br.estudos.importacao.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ResumoStepListener implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        long lidos = stepExecution.getReadCount();
        long filtrados = stepExecution.getFilterCount();

        long puladosLeitura = stepExecution.getReadSkipCount();
        long puladosProcessamento = stepExecution.getProcessSkipCount();
        long puladosEscrita = stepExecution.getWriteSkipCount();

        long gravados = stepExecution.getWriteCount();

        long dif = lidos - (filtrados + puladosProcessamento
                + puladosEscrita + gravados);

        log.info(
                "Step {} | status={} | lidos={} | filtrados={} | " +
                        "skipLeitura={} | skipProcessamento={} | " +
                        "skipEscrita={} | gravados={} | diferença={}",

                stepExecution.getStepName(),
                stepExecution.getStatus(),
                lidos,
                filtrados,
                puladosLeitura,
                puladosProcessamento,
                puladosEscrita,
                gravados,
                dif
        );

        if (dif != 0 ){
            log.warn("INCOSISTÊNCIA NO STEP {} | diferenca={}",  stepExecution.getStepName(), dif);
        }

        return ExitStatus.COMPLETED;
    }
}
