package com.br.estudos.importacao.model;

import java.math.BigDecimal;

public record ItemsResponse (Long id, String idPedido, Pedido pedido, Integer seq,
                             String sku, String titulo, String editora, Integer qtd, BigDecimal precoUnitario, BigDecimal valorItem){}
