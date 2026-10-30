package com.br.estudos.Sql;

public final class ConsultasLiquidacao {

    public static final String ITENS_PANINI_ENTREGUES = """
            SELECT i.id AS item_id,
                   i.id_pedido,
                   i.editora,
                   i.titulo,
                   i.qtd,
                   i.preco_unitario,
                   i.valor_item,
                   p.forma_pagamento,
                   p.status,
                   p.data_hora
              FROM item_pedido i
              JOIN pedido p
                ON p.id_pedido = i.id_pedido
             WHERE p.status = 'ENTREGUE'
               AND i.editora = 'Panini'
               AND NOT EXISTS (
                     SELECT 1
                       FROM liquidacao_panini l
                      WHERE l.item_pedido_id = i.id)
             ORDER BY i.id
            """;

    private ConsultasLiquidacao() {
    }
}