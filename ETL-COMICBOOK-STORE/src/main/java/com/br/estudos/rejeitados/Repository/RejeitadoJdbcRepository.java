package com.br.estudos.rejeitados.Repository;


import com.br.estudos.rejeitados.model.RegistroRejeitados;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;

@RequiredArgsConstructor
@Repository
public class RejeitadoJdbcRepository {

    private final JdbcClient jdbc;

    // Gerencia uma transação: confirma todas as operações no banco se tudo der certo (COMMIT)
    // ou desfaz as alterações se ocorrer uma falha (ROLLBACK).
    //REQUIRES_NEW significa que o Spring suspende uma transação existente (se houver) e inicia uma nova, independente dela.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void salvar (RegistroRejeitados r) {
        jdbc.sql("""
        INSERT INTO REGISTROS_REJEITADOS
        (TIPO_REGISTRO, LINHA_ARQUIVO, CONTEUDO, MOTIVO, ETAPA)
        VALUES (:tipoRegistro, :numeroLinha, :conteudoBruto, :mensagemErro, :etapa)
        """)
                .param("tipoRegistro", r.tipoRegistro())
                .param("numeroLinha", r.numeroLinha(), Types.NUMERIC)
                .param("conteudoBruto", r.conteudoBruto())
                .param("mensagemErro", r.mensagemErro())
                .param("etapa", r.fase().name())
                .update();
    }


}
