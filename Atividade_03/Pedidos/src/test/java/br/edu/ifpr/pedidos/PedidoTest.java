package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void deveCriarPedidoValido() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 2, 10, 500, false);

        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertEquals(1, pedido.itens().size());
        assertEquals("PR", pedido.uf());
        assertFalse(pedido.expresso());
        assertNull(pedido.cupom());
    }

    @Test
    void deveCalcularSubtotal() {
        ItemPedido item1 = new ItemPedido("SKU-1", 1000, 2, 10, 500, false);
        ItemPedido item2 = new ItemPedido("SKU-2", 2500, 3, 10, 300, false);

        Pedido pedido = new Pedido(List.of(item1, item2), "PR", false, null);

        assertEquals(9500, pedido.subtotalCentavos());
    }

    @Test
    void devePermitirListaVaziaNaCriacao() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertTrue(pedido.itens().isEmpty());
        assertEquals(0, pedido.subtotalCentavos());
    }

    @Test
    void deveFazerCopiaDefensivaDaLista() {
        var itens = new java.util.ArrayList<ItemPedido>();
        itens.add(new ItemPedido("SKU-1", 1000, 1, 10, 500, false));

        Pedido pedido = new Pedido(itens, "PR", false, null);

        itens.clear();

        assertEquals(1, pedido.itens().size());
    }

    @Test
    void deveAceitarCupomNulo() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertNull(pedido.cupom());
    }

    @Test
    void deveAceitarCupomEmBranco() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        Pedido pedido = new Pedido(List.of(item), "PR", false, "   ");

        assertEquals("   ", pedido.cupom());
    }

    @Test
    void deveLancarExcecaoParaListaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(null, "PR", false, null)
        );
    }

    @Test
    void deveLancarExcecaoParaMaisDe100Itens() {
        var itens = new java.util.ArrayList<ItemPedido>();

        for (int i = 0; i < 101; i++) {
            itens.add(new ItemPedido("SKU-" + i, 1000, 1, 10, 100, false));
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(itens, "PR", false, null)
        );
    }

    @Test
    void deveLancarExcecaoParaUfNula() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(List.of(item), null, false, null)
        );
    }

    @Test
    void deveLancarExcecaoParaUfComTamanhoInvalido() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(List.of(item), "P", false, null)
        );
    }

    @Test
    void deveLancarExcecaoParaUfMinuscula() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(List.of(item), "pr", false, null)
        );
    }

    @Test
    void deveLancarExcecaoParaUfComCaracterEspecial() {
        ItemPedido item = new ItemPedido("SKU-1", 1000, 1, 10, 500, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(List.of(item), "P1", false, null)
        );
    }
    @Test
    void deveIdentificarItemFragilAtivo() {
        ItemPedido item = new ItemPedido("SKU-1", 10_000, 1, 10, 500, true);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertTrue(pedido.temFragil());
    }

    @Test
    void naoDeveIdentificarItemFragilComQuantidadeZero() {
        ItemPedido item = new ItemPedido("SKU-1", 10_000, 0, 10, 500, true);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveIgnorarItemComQuantidadeZeroNoSubtotal() {
        ItemPedido itemInativo = new ItemPedido("SKU-1", 10_000, 0, 10, 500, false);
        ItemPedido itemAtivo = new ItemPedido("SKU-2", 20_000, 1, 10, 500, false);

        Pedido pedido = new Pedido(
                List.of(itemInativo, itemAtivo),
                "PR",
                false,
                null
        );

        assertEquals(20_000, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularSubtotalComItensAtivos() {
        ItemPedido item1 = new ItemPedido("SKU-1", 10_000, 2, 10, 500, false);
        ItemPedido item2 = new ItemPedido("SKU-2", 5_000, 1, 10, 500, false);

        Pedido pedido = new Pedido(
                List.of(item1, item2),
                "PR",
                false,
                null
        );

        assertEquals(25_000, pedido.subtotalCentavos());
    }
}