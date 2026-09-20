# Relatório do grupo

**Integrantes:** preencher com os nomes do grupo.

## Grafos e complexidade

### Grafo de chamadas

```text
PedidoService.fechar(pedido, cliente)
│
├── Pedido.subtotalCentavos()
├── Pedido.estoqueSuficiente()
├── PoliticaDesconto.calcular(cliente, subtotal, cupom)
├── CalculadoraFrete.calcular(pedido, cliente, liquido)
│   ├── Pedido.pesoGramas()
│   └── Pedido.temFragil()
├── AnaliseRisco.avaliar(cliente, total, expresso)
└── PagamentoService.pagar(total, 3)
    └── ProcessadorPagamento.autorizar(total)
```

O fluxo de `fechar` respeita a ordem definida no enunciado: validação das referências → cliente bloqueado → subtotal → estoque → desconto → frete → risco → pagamento. Os retornos antecipados impedem a execução das etapas posteriores.

### Modelo adotado para os CFGs

Foi utilizado um CFG com **saída unificada**. Condições de curto-circuito (`&&` e `||`) são modeladas como decisões separadas, pois o segundo operando pode ou não ser avaliado. No `switch` de `CalculadoraFrete`, os casos `SP` e `RJ` compartilham o mesmo bloco, enquanto `PR` e `default` possuem blocos próprios.

Tratamento de exceções foi representado como saída excepcional quando relevante, mas não foi tratado como branch do JaCoCo, conforme o enunciado.

| Método | Nós | Arestas | V(G) | Caminhos independentes | Restrições de viabilidade |
| --- | ---: | ---: | ---: | ---: | --- |
| `PoliticaDesconto.calcular` | 17 | 29 | 14 | 14 | Alguns caminhos do `switch` dependem do cupom; o teto só é alcançável quando o desconto calculado supera 20% |
| `CalculadoraFrete.calcular` | 14 | 21 | 9 | 9 | O laço pode executar zero, uma ou várias iterações; frete grátis depende de líquido e entrega normal |
| `AnaliseRisco.avaliar` | 13 | 20 | 9 | 9 | `||` e `&&` criam caminhos de curto-circuito; alguns caminhos não passam pelo `PedidoService` devido a retornos anteriores |
| `PagamentoService.pagar` | 9 | 12 | 5 | 5 | Retry ocorre somente para `IllegalStateException`; outras exceções propagam |
| `PedidoService.fechar` | 9 | 14 | 7 | 7 | Bloqueio, subtotal zero e falta de estoque encerram o fluxo antes do pagamento |

> **Observação sobre McCabe:** os valores acima seguem o CFG adotado neste relatório, com curto-circuitos modelados separadamente e saídas unificadas. Para `switch`, a complexidade considera suas alternativas de fluxo. O valor de McCabe é `V(G) = E − N + 2`.

### CFG — `PoliticaDesconto.calcular`

```text
[Entrada]
   |
[subtotal < 0?] --sim--> [Exceção]
   |
  não
   v
[cliente.vip?]
 ├─ sim --> [desconto = 10%]
 └─ não --> [subtotal >= 50000?]
              ├─ sim --> [desconto = 5%]
              └─ não --> [desconto = 0]
                         |
                         v
                  [cupom == null?]
                   ├─ sim --> [retorno]
                   └─ não --> [cupom.isBlank?]
                                ├─ sim --> [retorno]
                                └─ não --> [switch]
                                             ├─ BEMVINDO
                                             │    └─ compras == 0?
                                             │       └─ subtotal >= 10000?
                                             ├─ EXTRA10
                                             │    └─ subtotal >= 20000?
                                             └─ default --> [Exceção]
                                                  |
                                             [teto = 20%]
                                                  |
                                         [desconto > teto?]
                                           ├─ sim --> [teto]
                                           └─ não --> [desconto]
```

### CFG — `CalculadoraFrete.calcular`

```text
[Entrada]
   |
[liquido < 0?] --sim--> [Exceção]
   |
  não
   v
[switch UF]
 ├─ PR ------> [1200]
 ├─ SP/RJ ---> [2000]
 └─ default -> [3000]
        |
[peso - 2000]
        |
[excedente > 0?]
 ├─ não --------------------┐
 └─ sim -> [frete += 300]   |
          [excedente -=1000]|
             └──── volta ───┘
        |
[liquido >= 30000?]
 ├─ não --------------------┐
 └─ sim -> [expresso?]      |
             ├─ não -> [frete=0]
             └─ sim --------┘
        |
[cliente.vip?] --sim--> [frete /= 2]
        |
[pedido.expresso?] --sim--> [frete += 1500]
        |
[pedido.temFragil?] --sim--> [frete += 500]
        |
[retorno]
```

### CFG — `AnaliseRisco.avaliar`

```text
[Entrada]
   |
[total < 0?] --sim--> [Exceção]
   |
  não
   v
[cliente.bloqueado?]
 ├─ sim --> [RECUSADO]
 └─ não
       |
[comprasAnteriores == 0?]
 ├─ sim --> [total > 100000?]
 │            ├─ sim --> [REVISAO]
 │            └─ não --> [expresso?]
 │                         ├─ sim --> [REVISAO]
 │                         └─ não --> [APROVADO]
 └─ não --> [total > 500000?]
              ├─ não --> [APROVADO]
              └─ sim --> [!cliente.vip?]
                           ├─ sim --> [REVISAO]
                           └─ não --> [APROVADO]
```

### CFG — `PagamentoService.pagar`

```text
[Entrada]
   |
[total <= 0?] --sim--> [Exceção]
   |
  não
   v
[maxTentativas < 1?] --sim--> [Exceção]
   |
  não
   v
[maxTentativas > 3?] --sim--> [Exceção]
   |
  não
   v
[do]
 |
[tentativa++]
 |
[autorizar(total)]
 ├─ true/false --> [retorno]
 └─ IllegalStateException
          |
       [tentativa < limite?]
        ├─ sim --> volta ao `do`
        └─ não --> [false]
```

### CFG — `PedidoService.fechar`

```text
[Entrada]
   |
[requireNonNull(pedido)]
   |
[requireNonNull(cliente)]
   |
[cliente.bloqueado?]
 ├─ sim --> [BLOQUEADO / zeros]
 └─ não
       |
[subtotal]
       |
[subtotal == 0?]
 ├─ sim --> [IllegalArgumentException]
 └─ não
       |
[estoqueSuficiente?]
 ├─ não --> [SEM_ESTOQUE / zeros]
 └─ sim
       |
[desconto]
       |
[frete]
       |
[risco]
       |
[analise == APROVADO?]
 ├─ não --> [REVISAO / valores calculados]
 └─ sim
       |
[pagamentos.pagar(total, 3)?]
 ├─ sim --> [PAGO]
 └─ não --> [PAGAMENTO_RECUSADO]
```

### Base de caminhos independentes

A base abaixo representa caminhos independentes do CFG, não uma enumeração de todos os caminhos possíveis.

| Método | Base de caminhos |
| --- | --- |
| `PoliticaDesconto.calcular` | subtotal inválido; VIP; comum acima do limite; comum abaixo do limite; cupom nulo; cupom branco; BEMVINDO elegível; BEMVINDO não elegível; EXTRA10 elegível; EXTRA10 não elegível; cupom desconhecido; desconto acima do teto; desconto dentro do teto; normalização de cupom |
| `CalculadoraFrete.calcular` | líquido inválido; PR; SP/RJ; default; peso sem excedente; peso com iteração; frete grátis; VIP; expresso/frágil |
| `AnaliseRisco.avaliar` | total inválido; bloqueado; novo cliente com total alto; novo cliente sem total alto e expresso; novo cliente aprovado; recorrente com total não alto; recorrente alto VIP; recorrente alto não VIP; combinações de curto-circuito |
| `PagamentoService.pagar` | total inválido; limite inválido; autorização normal; retry; esgotamento/propagação |
| `PedidoService.fechar` | bloqueado; subtotal zero; sem estoque; revisão; pagamento aprovado; pagamento recusado; retry do pagamento |

## Matriz de testes

| ID / método JUnit | Unidade | Entrada e estado do stub | Resultado esperado | Caminho / aresta | Critério atendido |
| --- | --- | --- | --- | --- | --- |
| `deveRecusarClienteBloqueado` | `AnaliseRisco` | cliente bloqueado | `RECUSADO` | bloqueio | branch |
| `deveLancarExcecaoParaTotalNegativo` | `AnaliseRisco` | total negativo | `IllegalArgumentException` | validação | exceção |
| `deveColocarEmRevisaoClienteSemComprasComTotalAlto` | `AnaliseRisco` | novo, total > R$1.000 | `REVISAO` | limiar alto | branch |
| `deveColocarEmRevisaoClienteSemComprasComEntregaExpressa` | `AnaliseRisco` | novo + expresso | `REVISAO` | `||` | curto-circuito |
| `deveAprovarClienteSemComprasComTotalBaixoEEntregaNormal` | `AnaliseRisco` | novo + total baixo + normal | `APROVADO` | falso nos dois critérios | branch |
| `deveColocarEmRevisaoClienteComComprasNaoVipETotalAlto` | `AnaliseRisco` | recorrente + não VIP + total alto | `REVISAO` | `&&` verdadeiro | branch |
| `deveAprovarClienteVipMesmoComTotalAlto` | `AnaliseRisco` | recorrente + VIP + total alto | `APROVADO` | `!vip` falso | curto-circuito |
| `deveAprovarClienteComComprasComTotalAte500000` | `AnaliseRisco` | recorrente + total limite | `APROVADO` | limite | branch |
| `deveCalcularFrete...` | `CalculadoraFrete` | PR, SP/RJ, outras UFs, pesos e modalidades | fretes esperados | `switch`/loop | branches |
| `deveTentarNovamenteQuandoProcessadorEstiverIndisponivel` | `PagamentoService` | falha, falha, sucesso | `true`, 3 chamadas | retry | loop/catch |
| `deveRetornarFalseQuandoEsgotarTentativas` | `PagamentoService` | indisponível continuamente | `false`, 3 chamadas | esgotamento | loop |
| `devePropagarOutrasExcecoes` | `PagamentoService` | `RuntimeException` | exceção propagada | catch seletivo | exceção |
| `deveRejeitarTotalZero` | `PagamentoService` | total zero | `IllegalArgumentException` | validação | exceção |
| `deveRejeitarLimiteMenorQueUm` | `PagamentoService` | limite 0 | `IllegalArgumentException` | validação | `||` |
| `deveRejeitarLimiteMaiorQueTres` | `PagamentoService` | limite 4 | `IllegalArgumentException` | `||` | `||` |
| `deveDar10PorcentoParaVip` | `PoliticaDesconto` | VIP | 10% | ramo VIP | branch |
| `deveDar5PorcentoParaClienteComumAcimaDe500` | `PoliticaDesconto` | comum + >= R$500 | 5% | ramo comum | branch |
| `deveDarZeroParaClienteComumAbaixoDe500` | `PoliticaDesconto` | comum + < R$500 | 0 | limite | branch |
| `deveAplicarCupomBemVindo` | `PoliticaDesconto` | novo + BEMVINDO | +R$20 | cupom | branch |
| `naoDeveAplicarBemVindoComComprasAnteriores` | `PoliticaDesconto` | histórico > 0 | sem bônus | condição falsa | branch |
| `naoDeveAplicarBemVindoAbaixoDe100` | `PoliticaDesconto` | subtotal < R$100 | sem bônus | limite | branch |
| `deveAplicarCupomExtra10` | `PoliticaDesconto` | EXTRA10 elegível | +10% | case | branch |
| `naoDeveAplicarExtra10AbaixoDe200` | `PoliticaDesconto` | subtotal < R$200 | sem bônus | limite | branch |
| `deveAceitarCupomComEspacosEMinusculas` | `PoliticaDesconto` | `" extra10 "` | normalizado | `trim`/uppercase | normalização |
| `deveLancarExcecaoParaCupomDesconhecido` | `PoliticaDesconto` | cupom inválido | exceção | default | branch |
| `deveLimitarDescontoQuandoUltrapassar20Porcento` | `PoliticaDesconto` | VIP + BEMVINDO | teto de 20% | ternário | branch |
| `deveLancarExcecaoParaSubtotalNegativo` | `PoliticaDesconto` | subtotal < 0 | exceção | validação | exceção |
| `deveCalcularSubtotal` | `Pedido` | duas linhas ativas | subtotal correto | `for` | loop |
| `deveIgnorarItemComQuantidadeZeroNoSubtotal` | `Pedido` | uma linha inativa + uma ativa | somente ativa | `continue` | branch |
| `deveIdentificarItemFragilAtivo` | `Pedido` | item frágil ativo | `true` | `&&` | branch |
| `naoDeveIdentificarItemFragilComQuantidadeZero` | `Pedido` | item frágil inativo | `false` | curto-circuito | branch |
| `deveRecusarPedidoSemEstoque` | `PedidoService` | quantidade > estoque | `SEM_ESTOQUE` | retorno antecipado | branch |
| `deveColocarPedidoEmRevisao` | `PedidoService` | cliente novo + expresso | `REVISAO` | risco | colaboração |
| `deveRetornarPagamentoRecusadoQuandoProcessadorRecusar` | `PedidoService` | stub retorna `false` | `PAGAMENTO_RECUSADO` | pagamento | colaboração |
| `deveAceitarPagamentoAposNovaTentativa` | `PedidoService` | 1ª lança `IllegalStateException`, 2ª aprova | `PAGO`, 2 chamadas | retry | colaboração |

A suíte final executada contém **87 testes**.

## Evolução da cobertura

| Etapa | Testes executados | Linhas | Branches | Métodos | Classes | Lacunas e justificativas |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Inicial | 0 | Não medido | Não medido | Não medido | Não medido | Sem testes |
| Suíte intermediária | 81 | 100% | 96% | 100% | 100% | Branches restantes identificados em `Pedido` e `PoliticaDesconto` |
| Suíte final | 81 | 100% | 100% | 100% | 100% | Branches restantes cobertos, incluindo `Pedido.temFragil`, `Pedido.subtotalCentavos` e subtotal negativo em `PoliticaDesconto` |

## Análise crítica

### Quais combinações faltavam mesmo com os ramos cobertos?

Cobertura de branches não garante que todas as combinações de decisões tenham sido exercitadas. Em `CalculadoraFrete`, por exemplo, tarifa regional, peso excedente, frete grátis, VIP, expresso e fragilidade podem ser combinados de várias formas. A suíte cobre os ramos relevantes, mas não enumera todas as combinações possíveis.

### Quais condições não foram avaliadas devido ao curto-circuito?

Condições compostas usam curto-circuito. Em `AnaliseRisco`, por exemplo:

```java
total > 100_000 || expresso
```

Quando `total > 100_000` é verdadeiro, `expresso` não precisa ser avaliado. De forma semelhante:

```java
total > 500_000 && !cliente.vip()
```

Quando `total > 500_000` é falso, `!cliente.vip()` não é avaliado.

Por isso, foram utilizados testes que fazem o primeiro operando ser verdadeiro e falso, permitindo exercitar os comportamentos relevantes.

### Quais caminhos são inviáveis no serviço, mas viáveis na unidade?

`AnaliseRisco` pode ser testado isoladamente com combinações de cliente, total e entrega expressa. No `PedidoService`, porém, o risco somente é chamado depois de subtotal, estoque, desconto e frete. Um pedido bloqueado, vazio ou sem estoque retorna antes de chegar ao risco.

Assim, um caminho pode ser válido para o teste unitário de `AnaliseRisco`, mas não ser alcançável através de `PedidoService` com os mesmos estados.

### Como foram testadas exceções e quantidades de iterações?

Foram utilizados testes de exceção para entradas inválidas, incluindo subtotal negativo, total de pagamento inválido, limites inválidos de tentativa, UF inválida, lista inválida e histórico inválido.

Para iterações, `Pedido` foi testado com linhas ativas e inativas. `CalculadoraFrete` foi testada com peso abaixo/acima do limite e com peso que exige uma ou mais iterações. `PagamentoService` foi testado com sucesso, recusa, retry e esgotamento das tentativas.

### Qual alteração proposital foi detectada por qual teste?

Esta etapa deve ser registrada somente após realizar a alteração proposital no código de produção. O procedimento exigido pelo enunciado é alterar temporariamente uma regra, executar a suíte, identificar o teste que falhou e desfazer a alteração antes da entrega.

**Status:** não registrado no projeto fornecido; não foi inferido para evitar declarar uma mutação que não foi executada.

## Resultado final

A suíte final contém **87 testes passando** e o JaCoCo foi verificado com:

- **Lines: 100%**
- **Branches: 100%**
- **Methods: 100%**
- **Classes: 100%**

O resultado atende à meta de cobertura indicada no enunciado, mantendo a distinção entre cobertura de ramos e cobertura de caminhos completos.
