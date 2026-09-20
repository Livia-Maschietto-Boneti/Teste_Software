package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    @Test
    void deveCriarItemValido() {
        ItemPedido item = new ItemPedido(
                "SKU-001", 1000, 2, 10, 500, false
        );

        assertEquals("SKU-001", item.sku());
        assertEquals(1000, item.precoCentavos());
        assertEquals(2, item.quantidade());
        assertEquals(10, item.estoque());
        assertEquals(500, item.pesoGramas());
        assertFalse(item.fragil());
    }

    @Test
    void deveCalcularTotalCentavos() {
        ItemPedido item = new ItemPedido(
                "SKU-001", 2500, 4, 10, 500, false
        );

        assertEquals(10_000, item.totalCentavos());
    }

    @Test
    void deveConsiderarDisponivelQuandoQuantidadeIgualAoEstoque() {
        ItemPedido item = new ItemPedido(
                "SKU-001", 1000, 10, 10, 500, false
        );

        assertTrue(item.disponivel());
    }

    @Test
    void deveConsiderarIndisponivelQuandoQuantidadeMaiorQueEstoque() {
        ItemPedido item = new ItemPedido(
                "SKU-001", 1000, 11, 10, 500, false
        );

        assertFalse(item.disponivel());
    }

    @Test
    void deveAceitarQuantidadeZero() {
        ItemPedido item = new ItemPedido(
                "SKU-001", 1000, 0, 0, 500, false
        );

        assertEquals(0, item.totalCentavos());
        assertTrue(item.disponivel());
    }

    @Test
    void deveLancarExcecaoParaSkuNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido(null, 1000, 1, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaSkuEmBranco() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("   ", 1000, 1, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaPrecoZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 0, 1, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaPrecoAcimaDoLimite() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1_000_001, 1, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaQuantidadeNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1000, -1, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaQuantidadeAcimaDoLimite() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1000, 101, 10, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaEstoqueNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1000, 1, -1, 500, false)
        );
    }

    @Test
    void deveLancarExcecaoParaPesoZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1000, 1, 10, 0, false)
        );
    }

    @Test
    void deveLancarExcecaoParaPesoAcimaDoLimite() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("SKU-001", 1000, 1, 10, 100_001, false)
        );
    }
}