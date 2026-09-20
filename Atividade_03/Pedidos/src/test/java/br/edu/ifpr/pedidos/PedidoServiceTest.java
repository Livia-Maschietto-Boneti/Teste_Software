package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            // A lista comprova uma única cobrança, com o valor correto.
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveRecusarClienteBloqueado() {
        Cliente cliente = new Cliente(false, true, 1);
        ItemPedido item = new ItemPedido("SKU-1", 10_000, 1, 10, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("BLOQUEADO", resultado.status());
        assertEquals(0, resultado.subtotalCentavos());
        assertEquals(0, resultado.freteCentavos());
        assertEquals(0, resultado.totalCentavos());
    }

    @Test
    void deveRecusarPedidoSemEstoque() {
        Cliente cliente = new Cliente(false, false, 1);

        ItemPedido item = new ItemPedido(
                "SKU-1", 10_000, 5, 2, 500, false
        );

        Pedido pedido = new Pedido(
                List.of(item), "PR", false, null
        );

        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("SEM_ESTOQUE", resultado.status());
        assertEquals(0, resultado.totalCentavos());
    }

    @Test
    void deveColocarPedidoEmRevisao() {
        Cliente cliente = new Cliente(false, false, 0);

        ItemPedido item = new ItemPedido(
                "SKU-1", 10_000, 1, 10, 500, false
        );

        Pedido pedido = new Pedido(
                List.of(item), "PR", true, null
        );

        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("REVISAO", resultado.status());
    }
    @Test
    void deveRecusarPagamento() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("SKU-1", 10_000, 1, 10, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        PedidoService service = new PedidoService(total -> false);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("PAGAMENTO_RECUSADO", resultado.status());
    }
    @Test
    void deveLancarExcecaoParaPedidoSemItensAtivos() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        PedidoService service = new PedidoService(total -> true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.fechar(pedido, cliente)
        );
    }

    @Test
    void deveAprovarClienteComComprasMesmoComTotalMaiorQue1000() {
        Cliente cliente = new Cliente(false, false, 1);

        ItemPedido item = new ItemPedido(
                "SKU-1", 100_000, 1, 10, 500, false
        );

        Pedido pedido = new Pedido(
                List.of(item), "PR", false, null
        );

        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("PAGO", resultado.status());
    }

    @Test
    void deveRetornarPagamentoRecusadoQuandoProcessadorRecusar() {
        Cliente cliente = new Cliente(false, false, 1);

        ItemPedido item = new ItemPedido(
                "SKU-1", 10_000, 1, 10, 500, false
        );

        Pedido pedido = new Pedido(
                List.of(item), "PR", false, null
        );

        PedidoService service = new PedidoService(total -> false);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("PAGAMENTO_RECUSADO", resultado.status());
    }

    @Test
    void deveAceitarPagamentoAposNovaTentativa() {
        Cliente cliente = new Cliente(false, false, 1);

        ItemPedido item = new ItemPedido(
                "SKU-1", 10_000, 1, 10, 500, false
        );

        Pedido pedido = new Pedido(
                List.of(item), "PR", false, null
        );

        int[] chamadas = {0};

        PedidoService service = new PedidoService(total -> {
            chamadas[0]++;
            if (chamadas[0] == 1) {
                throw new IllegalStateException();
            }
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("PAGO", resultado.status());
        assertEquals(2, chamadas[0]);
    }
}