package com.br.estudos.Dominio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ITEM_PEDIDO")
public class Items {
    // O arquivo não traz id único por item: o banco gera a chave (via sequence)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_pedido_seq")
    @SequenceGenerator(name = "item_pedido_seq", sequenceName = "ITEM_PEDIDO_SEQ", allocationSize = 50)
    @Column(name = "ID")
    private Long id;

    // Coluna que recebe o id do pedido vindo do arquivo (ex.: P000001)
    @NotNull
    @Column(name = "ID_PEDIDO", length = 20)
    private String idPedido;

    // FK: cada item pertence a um pedido (N itens -> 1 pedido)
    @ManyToOne(fetch = FetchType.LAZY) //carrega o pedido só quando acessado
    //diz ao jpa que o pedido não escreve nessa coluna só le, sem isso o hibernate alerta de column mapeada 2x
    @JoinColumn(name = "ID_PEDIDO", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

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