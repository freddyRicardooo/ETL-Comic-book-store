package com.br.estudos.importacao.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Pedido {
    @NotNull
    private String idPedido;
    @NotNull
    private LocalDateTime dataHora;
    @NotNull
    private String idCliente;
    @NotNull
    private String nome;
    @NotNull
    private String email;
    @NotNull
    private String status;
    @NotNull
    private String formaPagamento;
    @NotNull
    private BigDecimal valorTotal;
    @NotNull
    private BigDecimal frete;
    @NotNull
    private BigDecimal desconto;
}