
package com.br.estudos.importacao.service;

import com.br.estudos.importacao.model.StatusArquivo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ControleArquivoService {

    private final JdbcTemplate jdbcTemplate;

    public ControleArquivoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Tenta registrar um novo arquivo para processamento.
     *
     * Primeiro tenta inserir o hash na tabela CONTROLE_ARQUIVO.
     *
     * Se o hash ainda não existir:
     * - Cria um registro com status PROCESSANDO.
     *
     * Se o hash já existir:
     * - O Oracle rejeita a inserção por causa da restrição UNIQUE.
     * - Capturamos a DuplicateKeyException.
     * - Tentamos atualizar o registro existente somente se
     *   seu status atual for ERRO.
     *
     * Dessa forma:
     * - PROCESSADO: bloqueia processamento duplicado.
     * - PROCESSANDO: bloqueia execução concorrente.
     * - ERRO: permite uma nova tentativa.
     *
     * O JOB_EXECUTION_ID identifica qual execução do Spring Batch
     * assumiu o controle daquele arquivo.
     */

    @Transactional
    public void iniciarProcessamento(
            String hashArquivo,
            String nomeArquivo,
            Long jobExecutionId) {

        String sql = """
                INSERT INTO CONTROLE_ARQUIVO (
                    HASH_ARQUIVO,
                    NOME_ARQUIVO,
                    STATUS,
                    DATA_INICIO,
                    JOB_EXECUTION_ID
                )
                VALUES (?, ?, ?, SYSTIMESTAMP, ?)
                """;

        try {

            jdbcTemplate.update(
                    sql,
                    hashArquivo,
                    nomeArquivo,
                    StatusArquivo.PROCESSANDO.name(),
                    jobExecutionId
            );

        } catch (DuplicateKeyException exception) {

            /*
             * O hash já existe no banco.
             *
             * Tentamos reutilizar o registro somente se o arquivo
             * estiver com status ERRO.
             */

            String atualizarSql = """
                    UPDATE CONTROLE_ARQUIVO
                    SET STATUS = ?,
                        DATA_INICIO = SYSTIMESTAMP,
                        DATA_FIM = NULL,
                        MENSAGEM_ERRO = NULL,
                        JOB_EXECUTION_ID = ?
                    WHERE HASH_ARQUIVO = ?
                    AND STATUS = ?
                    """;

            int linhasAtualizadas = jdbcTemplate.update(
                    atualizarSql,
                    StatusArquivo.PROCESSANDO.name(),
                    jobExecutionId,
                    hashArquivo,
                    StatusArquivo.ERRO.name()
            );

            /*
             * Se nenhuma linha foi atualizada, significa que
             * o arquivo não estava disponível para reprocessamento.
             *
             * Consultamos seu status atual para identificar
             * o motivo do bloqueio.
             */

            if (linhasAtualizadas == 0) {

                String statusAtual = jdbcTemplate.queryForObject(
                        """
                        SELECT STATUS
                        FROM CONTROLE_ARQUIVO
                        WHERE HASH_ARQUIVO = ?
                        """,
                        String.class,
                        hashArquivo
                );

                throw new IllegalStateException(
                        "Arquivo não pode ser processado. Status atual: "
                                + statusAtual
                );
            }
        }
    }

    @Transactional
    public void marcarComoProcessado(
            String hashArquivo,
            Long jobExecutionId) {

        String sql = """
                UPDATE CONTROLE_ARQUIVO
                SET STATUS = ?,
                    DATA_FIM = SYSTIMESTAMP,
                    MENSAGEM_ERRO = NULL
                WHERE HASH_ARQUIVO = ?
                AND STATUS = ?
                AND JOB_EXECUTION_ID = ?
                """;

        int linhasAtualizadas = jdbcTemplate.update(
                sql,
                StatusArquivo.PROCESSADO.name(),
                hashArquivo,
                StatusArquivo.PROCESSANDO.name(),
                jobExecutionId
        );

        validarAtualizacao(linhasAtualizadas);
    }

    @Transactional
    public void marcarComoErro(
            String hashArquivo,
            Long jobExecutionId,
            String mensagemErro) {

        String sql = """
                UPDATE CONTROLE_ARQUIVO
                SET STATUS = ?,
                    DATA_FIM = SYSTIMESTAMP,
                    MENSAGEM_ERRO = ?
                WHERE HASH_ARQUIVO = ?
                AND STATUS = ?
                AND JOB_EXECUTION_ID = ?
                """;

        int linhasAtualizadas = jdbcTemplate.update(
                sql,
                StatusArquivo.ERRO.name(),
                mensagemErro,
                hashArquivo,
                StatusArquivo.PROCESSANDO.name(),
                jobExecutionId
        );

        validarAtualizacao(linhasAtualizadas);
    }

    private void validarAtualizacao(int linhasAtualizadas) {

        if (linhasAtualizadas == 0) {
            throw new IllegalStateException(
                    "Não foi possível atualizar o controle do arquivo."
            );
        }
    }
}
