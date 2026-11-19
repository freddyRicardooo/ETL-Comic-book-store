package com.br.estudos.importacao.processor;


import com.br.estudos.importacao.model.Items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Sobe um contexto Spring só com a config do processor + o escopo de step abaixo
@SpringJUnitConfig({
        VendasQuadrinhosValidacaoProcessorConfig.class,
        ItemValidacaoProcessorIT.EscopoDeStepConfig.class
})
// Abre/fecha um step "falso" em cada teste (usa o getStepExecution() abaixo)
@TestExecutionListeners(listeners = StepScopeTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class ItemValidacaoProcessorIT {

    // Registra o escopo "step" no contexto de teste
    @Configuration
    static class EscopoDeStepConfig {

        @Bean
        static StepScope stepScope() {
            return new StepScope();
        }
    }

    // Processor real que o step usa; ele escolhe o delegate de Pedido ou de Items
    @Autowired
    private ItemProcessor<Object, Object> vendasQuadrinhosClassifierProcessor;

    // Ids de PEDIDOS já aceitos. O processor de Item só consulta este Set, nunca grava nele.
    @Autowired
    private Set<String> pedidosAceitos;

    // O listener usa este método para abrir o step falso
    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    // Item válido padrão: cada teste altera só o campo que está testando
    private Items items() {
        Items items = new Items();
        items.setId(1L);
        items.setEditora("Panini");
        items.setValorItem(new BigDecimal("15.00"));
        items.setQtd(4);
        items.setSeq(72375);
        items.setPrecoUnitario(new BigDecimal("20.00"));
        items.setIdPedido("P000273");
        items.setSku("HQ0004");
        return items;
    }

    // Roda antes de cada teste: deixa o pedido do item como "já aceito".
    // Assim, qualquer rejeição nos testes abaixo só pode ser pela regra que está sendo testada.
    @BeforeEach
    void pedidoAceito() {
        pedidosAceitos.add("P000273");
    }

    @Test
    void aceitaItemValido() throws Exception {
        Object resultado = vendasQuadrinhosClassifierProcessor.process(items());

        assertEquals("P000273", ((Items) resultado).getIdPedido());
        assertEquals("HQ0004", ((Items) resultado).getSku());
    }

    // Roda uma vez para cada valor. A regra rejeita preço <= 0 (0.01 seria válido).
    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01"})
    void rejeitaPrecoUnitarioZeradoOuNegativo(String preco) {
        Items items = items();
        items.setPrecoUnitario(new BigDecimal(preco));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> vendasQuadrinhosClassifierProcessor.process(items));

        // Garante que foi POR CAUSA DO PREÇO, e não por outra regra
        assertTrue(ex.getMessage().contains("Preço"));
    }

    @Test
    void rejeitaItemDuplicado() throws Exception {
        vendasQuadrinhosClassifierProcessor.process(items());

        ValidationException ex = assertThrows(ValidationException.class,
                () -> vendasQuadrinhosClassifierProcessor.process(items()));

        assertTrue(ex.getMessage().contains("duplicado"));
    }

    @Test
    void rejeitaItemSemPedidoAceito() {
        pedidosAceitos.clear();   // desfaz o que o @BeforeEach colocou

        ValidationException ex = assertThrows(ValidationException.class,
                () -> vendasQuadrinhosClassifierProcessor.process(items()));

        assertTrue(ex.getMessage().contains("sem pedido válido"));
    }
}
