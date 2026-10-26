package com.br.estudos.Dominio;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Pedido {
    private String idPedido;
    private LocalDateTime dataHora;
    private String idCliente;
    private String nome;
    private String email;
    private String status;
    private String formaPagamento;
    private BigDecimal valorTotal;
    private BigDecimal frete;
    private BigDecimal desconto;
}
