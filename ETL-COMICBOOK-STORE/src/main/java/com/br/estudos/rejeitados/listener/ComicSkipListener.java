package com.br.estudos.rejeitados.listener;

import com.br.estudos.rejeitados.model.FaseErro;
import com.br.estudos.rejeitados.Service.RejeitadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;


/**
 é o ouvido do batch. Quando o spring batch pula uma linha, ele chama este listener, e o listener só repassa os dados
 pro service gravar. Nunca chama o listener: o framwork chama
 */


@Component
@RequiredArgsConstructor
public class ComicSkipListener implements SkipListener<Object, Object> {
    private static final String STEP = "importacaoPedidosQuadrinhosStep";

    private final RejeitadoService rejeitadoService;

    @Override
    //dispara quando: linha elegivel, campo faltando, falha na leitura. recebe: erro
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException ex){
            String bruto = ex.getInput(); //texto bruto que reader tentou converter e não conseguiu
            rejeitadoService.registrar(STEP, FaseErro.LEITURA, extrairTipo(bruto), ex.getLineNumber(), bruto, t);
        }else { //Qualquer outra exceção que caia em onSkipInRead não tem esses dados. Não há oq preencher
            rejeitadoService.registrar(STEP, FaseErro.LEITURA, null, null, null, t);
        }
    }

    //dispara quando: processor rejeitou. recebe: item e o erro
    @Override
    public void onSkipInProcess(Object items, Throwable t) {
        rejeitadoService.registrar(STEP, FaseErro.PROCESSAMENTO, items.getClass().getSimpleName().toUpperCase(), null, String.valueOf(items), t);
    }

    @Override
    //dispara quando: falha ao gravar no banco. recebe: item e o erro
    public void onSkipInWrite(Object item, Throwable t) {
        rejeitadoService.registrar(STEP, FaseErro.ESCRITA, item.getClass().getSimpleName().toUpperCase(), null, String.valueOf(item), t);

    }

    private String extrairTipo(String linha){
        if (linha == null || linha.isBlank()) return null;
        return linha.split(";")[0].trim(); //divide a string por ; [0] seleciona a primeira parte trim() remove os espaços das extremidades
    }
}
