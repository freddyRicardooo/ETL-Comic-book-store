package com.br.estudos.importacao.processor;

import com.br.estudos.importacao.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * TESTE DE INTEGRAÇÃO (sem banco).
 *
 * Diferença para o teste unitário que existia aqui:
 *  - o Spring cria os beans de verdade (processor do pedido, do item, o Set pedidosAceitos
 *    e o classifier que escolhe qual processor usar);
 *  - o teste entra pelo bean que o step usa (vendasQuadrinhosClassifierProcessor), então
 *    valida o conjunto funcionando junto, e não só a regra isolada.
 *
 * Esses beans são @StepScope: só existem "dentro de um step". O StepScopeTestExecutionListener
 * abre um step falso antes de cada teste (usando o getStepExecution() abaixo) e fecha no fim.
 * Como cada teste ganha um step novo, o estado (duplicados, pedidosAceitos) começa limpo.
 */

// @SpringJUnitConfig: liga o JUnit 5 ao Spring e sobe um contexto SÓ com as classes listadas
// (a config do processor + a config de escopo abaixo), em vez da aplicação inteira.
@SpringJUnitConfig({
        VendasQuadrinhosValidacaoProcessorConfig.class,
        PedidoValidacaoProcessorIntegrationTest.EscopoDeStepConfig.class
})
// @TestExecutionListeners: adiciona o listener que abre/fecha um step falso em cada teste.
// MERGE_WITH_DEFAULTS mantém os listeners padrão do Spring junto com o nosso.
@TestExecutionListeners(listeners = StepScopeTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class PedidoValidacaoProcessorIntegrationTest {

    // @Configuration: classe de configuração só do teste, que o Spring lê para criar beans.
    @Configuration
    static class EscopoDeStepConfig {

        // @Bean: o retorno do método vira um bean do contexto.
        // Aqui registra o escopo "step" (o Boot faz isso sozinho na aplicação real).
        // É static porque é um BeanFactoryPostProcessor e precisa existir antes dos outros beans.
        @Bean
        static StepScope stepScope() {
            return new StepScope();
        }
    }

    // @Autowired: o Spring injeta aqui o bean real. O nome do campo desempata com o
    // BeanValidatingItemProcessor, que tem o mesmo tipo genérico.
    @Autowired
    private ItemProcessor<Object, Object> vendasQuadrinhosClassifierProcessor;

    // @Autowired: o Set que guarda os ids de pedidos aceitos (o processor de Item consulta ele).
    @Autowired
    private Set<String> pedidosAceitos;

    // O listener procura um método com este nome para saber qual step "falso" abrir antes do teste.
    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    // Monta um pedido válido padrão: cada teste altera só o campo que está testando.
    private Pedido pedidoValido() {
        Pedido p = new Pedido();
        p.setIdPedido("P1");
        p.setStatus("ENTREGUE");
        p.setFormaPagamento("CARTAO");
        p.setEmail("cliente@teste.com");
        p.setFrete(new BigDecimal("15.00"));
        return p;
    }

    // @Test: marca o método como um teste que o JUnit deve executar.
    // Caminho feliz: pedido válido passa e o id fica registrado em pedidosAceitos.
    @Test
    void aceitaPedidoValidoEGuardaOIdEmPedidosAceitos() throws Exception {
        Object resultado = vendasQuadrinhosClassifierProcessor.process(pedidoValido());

        assertEquals("P1", ((Pedido) resultado).getIdPedido());
        // É esse Set que o processor de Item consulta depois. Aqui está a "cola" entre os dois.
        assertTrue(pedidosAceitos.contains("P1"));
    }

    // Frete acima do teto (20,00) deve lançar ValidationException e não registrar o id.
    @Test
    void rejeitaFreteAcimaDoTetoENaoGuardaOId() {
        Pedido p = pedidoValido();
        p.setFrete(new BigDecimal("20.01"));

        assertThrows(ValidationException.class, () -> vendasQuadrinhosClassifierProcessor.process(p));
        assertFalse(pedidosAceitos.contains("P1"));
    }

    // Processar o mesmo pedido duas vezes: a segunda deve ser rejeitada como duplicada.
    @Test
    void rejeitaPedidoDuplicado() throws Exception {
        vendasQuadrinhosClassifierProcessor.process(pedidoValido());

        assertThrows(ValidationException.class,
                () -> vendasQuadrinhosClassifierProcessor.process(pedidoValido()));
    }

    // O classifier só conhece Pedido e Items: qualquer outro tipo deve estourar erro.
    @Test
    void rejeitaTipoNaoSuportado() {
        assertThrows(IllegalArgumentException.class,
                () -> vendasQuadrinhosClassifierProcessor.process("não sou Pedido nem Item"));
    }
}
