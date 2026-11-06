package com.br.estudos.liquidacao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemLiquidacaoLeitura (
     Long itemId,
     String idPedido,
     String editora,
     String titulo,
     Integer qtd,
     BigDecimal precoUnitario,
     BigDecimal valorItem,
     String formaPagamento,
     String status,
     LocalDateTime dataHora
){
}
