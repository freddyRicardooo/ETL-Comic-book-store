package com.br.estudos.importacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PEDIDO")
public class Pedido {

    @Id
    @NotNull
    @Column(name = "ID_PEDIDO", length = 20)
    private String idPedido;

    @NotNull
    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @NotNull
    @Column(name = "ID_CLIENTE", length = 20)
    private String idCliente;

    @NotNull
    @Column(name = "NOME", length = 100)
    private String nome;

    @NotNull
    @Column(name = "EMAIL", length = 150)
    private String email;

    @NotNull
    @Column(name = "STATUS", length = 30)
    private String status;

    @NotNull
    @Column(name = "FORMA_PAGAMENTO", length = 30)
    private String formaPagamento;

    @NotNull
    @Column(name = "VALOR_TOTAL", precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @NotNull
    @Column(name = "FRETE", precision = 10, scale = 2)
    private BigDecimal frete;

    @NotNull
    @Column(name = "DESCONTO", precision = 5, scale = 2)
    private BigDecimal desconto;
}