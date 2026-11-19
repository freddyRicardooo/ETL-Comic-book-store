# ETL Comic Book Store

Projeto de estudos de ETL (Extract, Transform, Load) desenvolvido com **Spring Batch 6** e **Spring Boot 4.1**. A aplicação lê um arquivo texto com pedidos e itens de vendas de uma loja de quadrinhos, valida os registros, grava os dados válidos no Oracle e, em seguida, calcula a liquidação (repasse) das vendas da editora Panini.

Este é um projeto voltado para aprendizado do framework Spring Batch, cobrindo jobs, steps, processamento em chunks, tolerância a falhas, controle de idempotência e monitoramento de execuções.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Formato do arquivo de entrada](#formato-do-arquivo-de-entrada)
- [Modelo de dados](#modelo-de-dados)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Como executar](#como-executar)
- [Testes](#testes)
- [Documentação adicional](#documentação-adicional)

## Funcionalidades

- Cálculo de hash SHA-256 do arquivo de entrada para identificar o conteúdo.
- Controle de processamento por arquivo, impedindo reprocessamento de arquivos já concluídos e execuções concorrentes.
- Leitura de registros de pedido (`PEDIDO`) e item (`ITEM`) em formato delimitado por `;`.
- Validação de regras de negócio, como pedidos duplicados, frete acima do teto, status não entregues, pagamento via PIX, e-mail ausente e itens órfãos.
- Gravação em lote no Oracle com `JdbcBatchItemWriter`.
- Tolerância a falhas por registro, com limite máximo de itens ignorados por execução.
- Auditoria de registros rejeitados, com o conteúdo original, a etapa do erro e a mensagem.
- Liquidação automática das vendas da Panini (20% para a loja e 80% para a editora), sem duplicar vendas já liquidadas.
- Logs por execução com identificador do job no MDC e resumo estatístico por step.

## Arquitetura

O job principal, `jobImportacao`, executa três steps em sequência:

```
arquivo .txt
     |
     v
+-------------------------------+
| 1. registrarArquivoStep       |  Tasklet: calcula SHA-256 e registra o arquivo
+-------------------------------+  em CONTROLE_ARQUIVO (status PROCESSANDO)
     |
     v
+-------------------------------+
| 2. stepComicBooks             |  Chunk (1000): Reader -> Processor -> Writer
+-------------------------------+  Grava PEDIDO e ITEM_PEDIDO; rejeitados em REGISTROS_REJEITADOS
     |
     v
+-------------------------------+
| 3. vendasQuadrinhasLiquidadas |  Chunk (1000): cursor SQL -> Processor -> Writer
| Step                          |  Grava LIQUIDACAO_PANINI
+-------------------------------+
     |
     v
afterJob: CONTROLE_ARQUIVO recebe PROCESSADO ou ERRO
```

Principais componentes:

| Camada | Componentes |
|---|---|
| Job | `ImportacaoVendasQuadrinhosJobConfig`, `LiquidacaoPaniniJobConfig` |
| Steps | `RegistrarArquivoStepConfig`, `ImportacaoPedidosQuadrinhosStepConfig`, `VendasQuadrinhasLiquidadasStepConfig` |
| Reader | `FlatFileItemReader` (arquivo), `JdbcCursorItemReader` (banco) |
| Processor | `ClassifierCompositeItemProcessor` com validadores de pedido e item, `LiquidacaoPaniniProcessor` |
| Writer | `JdbcBatchItemWriter` para pedido, item e liquidação |
| Listeners | `JobLogListener`, `ControleArquivoJobListener`, `ResumoStepListener`, `ComicSkipListener` |
| Serviços | `HashArquivoService`, `ControleArquivoService`, `RejeitadoService` |

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 4.1.2 | Base da aplicação |
| Spring Batch | 6 (via starter) | Jobs, steps e processamento em lote |
| Spring JDBC | via starter | Acesso ao banco |
| Flyway | via starter | Versionamento do esquema |
| Oracle Database | Free (PDB `FREEPDB1`) | Banco de dados |
| Lombok | via parent | Redução de boilerplate |
| Maven | Wrapper incluso (`mvnw`) | Build e execução |

## Estrutura do projeto

O código está no módulo `ETL-COMICBOOK-STORE/`.

```
ETL-COMICBOOK-STORE/
├── pom.xml
├── mvnw / mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/br/estudos/
│   │   │   ├── importacao/      # job principal: arquivo -> PEDIDO / ITEM_PEDIDO
│   │   │   │   ├── job/
│   │   │   │   ├── step/
│   │   │   │   ├── tasklet/
│   │   │   │   ├── reader/
│   │   │   │   ├── processor/
│   │   │   │   ├── writer/
│   │   │   │   ├── listener/
│   │   │   │   ├── service/
│   │   │   │   └── model/
│   │   │   ├── liquidacao/      # liquidação das vendas da Panini
│   │   │   └── rejeitados/      # auditoria de registros recusados
│   │   └── resources/
│   │       ├── application.yml
│   │       └── DB/migration/    # scripts Flyway V1 a V4
│   └── test/
│       └── java/com/br/estudos/
└── EXPLICACAO_PROJETO.md        # explicação detalhada do projeto
```

## Formato do arquivo de entrada

Arquivo texto, uma linha por registro, campos separados por `;`. Cada linha começa com o tipo do registro.

```
PEDIDO;<idPedido>;<dataHora>;<idCliente>;<nome>;<email>;<status>;<formaPagamento>;<valorTotal>;<frete>;<desconto>
ITEM;<idPedido>;<seq>;<sku>;<titulo>;<editora>;<qtd>;<precoUnitario>;<valorItem>
```

- `dataHora` no formato `yyyy-MM-dd HH:mm:ss`.
- Valores decimais com ponto, por exemplo `29.90`.
- Os itens devem aparecer depois do pedido ao qual pertencem. Itens cujo pedido não foi aceito são rejeitados.

Exemplo:

```
PEDIDO;1001;2026-03-10 14:30:00;C01;Maria Silva;maria@exemplo.com;ENTREGUE;CARTAO;79.90;12.00;0.00
ITEM;1001;1;SKU-100;Batman Ano Um;Panini;2;29.90;59.80
ITEM;1001;2;SKU-200;Watchmen;Panini;1;19.90;19.90
```

## Modelo de dados

As migrations ficam em `src/main/resources/DB/migration` e são aplicadas pelo Flyway.

| Versão | Script | Tabelas |
|---|---|---|
| V1 | `V1__criar_controle_arquivo.sql` | `CONTROLE_ARQUIVO` |
| V2 | `V2__criar_pedido_e_item_pedido.sql` | `PEDIDO`, `ITEM_PEDIDO` |
| V3 | `V3__criar_registros_rejeitados.sql` | `REGISTROS_REJEITADOS` |
| V4 | `V4__criar_liquidacao_panini.sql` | `LIQUIDACAO_PANINI` |

As tabelas de metadados do Spring Batch (`BATCH_*`) não fazem parte das migrations. Elas são criadas automaticamente pelo framework, com `spring.batch.jdbc.initialize-schema: always`.

## Pré-requisitos

- JDK 21
- Oracle Database acessível (o projeto está configurado para `localhost:1522/FREEPDB1`)
- Maven não é necessário, pois o projeto inclui o Maven Wrapper

## Configuração

As configurações estão em `src/main/resources/application.yml`.

| Propriedade | Descrição | Padrão |
|---|---|---|
| `spring.datasource.url` | URL JDBC do Oracle | `jdbc:oracle:thin:@//localhost:1522/FREEPDB1` |
| `spring.datasource.username` | Usuário do banco | `${DB_USER}` |
| `spring.datasource.password` | Senha do banco | `${DB_PASSWORD}` |
| `spring.batch.job.name` | Job executado na inicialização | `jobImportacao` |
| `importacao.diretorio` | Pasta onde fica o arquivo de entrada | `C:/Arquivo Comic Book` |

As credenciais são lidas de variáveis de ambiente e não devem ser versionadas:

```powershell
$env:DB_USER = "seu_usuario"
$env:DB_PASSWORD = "sua_senha"
```

## Como executar

1. Garanta que o Oracle esteja em execução e que as variáveis `DB_USER` e `DB_PASSWORD` estejam definidas.
2. Copie o arquivo de entrada para o diretório configurado em `importacao.diretorio`, com o nome `ArquivoComicBookStore.txt`.
3. Entre no módulo e execute a aplicação:

```bash
cd ETL-COMICBOOK-STORE
./mvnw spring-boot:run
```

Para processar outro arquivo, informe o parâmetro `arquivoComicBook`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--arquivoComicBook=outro_arquivo.txt
```

Para gerar o pacote executável:

```bash
./mvnw clean package
java -jar target/ETL-COMICBOOK-STORE-0.0.1-SNAPSHOT.jar
```

### Comportamento de reprocessamento

- Um arquivo com o mesmo conteúdo de um arquivo já `PROCESSADO` não é processado novamente.
- Um arquivo cuja última execução terminou em `ERRO` pode ser processado de novo.
- Um arquivo que está `PROCESSANDO` bloqueia novas execuções até que a anterior termine.

## Testes

```bash
cd ETL-COMICBOOK-STORE
./mvnw test
```

Os testes atuais cobrem o cálculo de hash (`HashArquivoTest`) e a carga do contexto da aplicação. Testes de steps e jobs ainda não foram implementados; o módulo `spring-boot-starter-batch-test` já está no projeto para isso.

## Documentação adicional

O arquivo [EXPLICACAO_PROJETO.md](ETL-COMICBOOK-STORE/EXPLICACAO_PROJETO.md) traz uma explicação detalhada de cada etapa, dos conceitos do Spring Batch aplicados e de pontos de melhoria identificados no código.
