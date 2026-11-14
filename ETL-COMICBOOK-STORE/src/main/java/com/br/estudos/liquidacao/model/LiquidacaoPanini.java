package com.br.estudos.liquidacao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class LiquidacaoPanini {

    private Long id;
    private Long itemPedidoId;
    private String editora;
    private String titulo;
    private String formaPagamento;
    private Integer qtd;
    private BigDecimal precoUnitario;
    private BigDecimal valorBruto;
    private BigDecimal percentualLoja;
    private BigDecimal valorLoja;
    private BigDecimal valorEditora;
    private String statusLiquidacao;
    private LocalDateTime dataVenda;
    private LocalDateTime dataLiquidacao;
}
