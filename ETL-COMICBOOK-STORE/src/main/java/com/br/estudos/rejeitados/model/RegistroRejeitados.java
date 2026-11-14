package com.br.estudos.rejeitados.model;

import java.time.LocalDateTime;

public record RegistroRejeitados(
        String nomeStep,
        FaseErro fase,
        String tipoRegistro,   // PEDIDO ou ITEM
        Long numeroLinha,
        String conteudoBruto,
        String mensagemErro,
        String tipoExcecao,
        LocalDateTime dataHora
) {}