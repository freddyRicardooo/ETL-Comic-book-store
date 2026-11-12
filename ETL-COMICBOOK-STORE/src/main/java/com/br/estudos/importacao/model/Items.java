package com.br.estudos.importacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
@Data
@Entity
@Immutable                       // org.hibernate.annotations.Immutable: o Hibernate nunca grava
@Table(name = "ITEM_PEDIDO")
public class Items {

    @Id
    @Column(name = "ID")
    private Long id;             // sem @GeneratedValue: quem gera é o banco (identity)

    @NotNull @Column(name = "ID_PEDIDO") private String idPedido;
    @NotNull @Column(name = "SEQ")       private Integer seq;
    @NotNull @Column(name = "SKU")       private String sku;
    @NotNull @Column(name = "TITULO")    private String titulo;
    @NotNull @Column(name = "EDITORA")   private String editora;
    @NotNull @Column(name = "QTD")       private Integer qtd;
    @NotNull @Column(name = "PRECO_UNITARIO") private BigDecimal precoUnitario;
    @NotNull @Column(name = "VALOR_ITEM")     private BigDecimal valorItem;
}