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

        long lidos = stepExecution.getReadCount(); //lidos com sucesso e entregue ao processor
        long filtrados = stepExecution.getFilterCount(); //itens q o processor retornou null

        long puladosLeitura = stepExecution.getReadSkipCount(); //exceção durante leitura, linha mal formatada ou FlatFileParseException
        long puladosProcessamento = stepExecution.getProcessSkipCount(); //exceção dentro do processor, item lido com sucesso e descartado pelo processor
        long puladosEscrita = stepExecution.getWriteSkipCount(); //exceção dentro do writter, item lido e processado mas a gravação falhou

        long gravados = stepExecution.getWriteCount(); //itens que o writer gravou com sucesso

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
