package com.br.estudos.Dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ITEM_PEDIDO")
public class Items {

    // O arquivo não traz id único por item: o banco gera a chave
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "ID_PEDIDO", length = 20)
    private String idPedido;

    @NotNull
    @Column(name = "SEQ")
    private Integer seq;

    @NotNull
    @Column(name = "SKU", length = 20)
    private String sku;

    @NotNull
    @Column(name = "TITULO", length = 150)
    private String titulo;

    @NotNull
    @Column(name = "EDITORA", length = 100)
    private String editora;

    @NotNull
    @Column(name = "QTD")
    private Integer qtd;

    @NotNull
    @Column(name = "PRECO_UNITARIO", precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @NotNull
    @Column(name = "VALOR_ITEM", precision = 12, scale = 2)
    private BigDecimal valorItem;
}