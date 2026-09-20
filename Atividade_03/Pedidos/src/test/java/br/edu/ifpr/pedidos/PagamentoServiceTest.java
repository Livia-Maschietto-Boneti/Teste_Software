package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    @Test
    void deveAprovarPagamento() {
        PagamentoService service = new PagamentoService(total -> true);

        boolean resultado = service.pagar(10_000, 1);

        assertTrue(resultado);
    }

    @Test
    void deveRecusarPagamentoSemRepetir() {
        int[] chamadas = {0};

        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            return false;
        });

        boolean resultado = service.pagar(10_000, 3);

        assertFalse(resultado);
        assertEquals(1, chamadas[0]);
    }

    @Test
    void deveTentarNovamenteQuandoProcessadorEstiverIndisponivel() {
        int[] chamadas = {0};

        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            if (chamadas[0] < 3) {
                throw new IllegalStateException();
            }
            return true;
        });

        boolean resultado = service.pagar(10_000, 3);

        assertTrue(resultado);
        assertEquals(3, chamadas[0]);
    }

    @Test
    void deveRetornarFalseQuandoEsgotarTentativas() {
        int[] chamadas = {0};

        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            throw new IllegalStateException();
        });

        boolean resultado = service.pagar(10_000, 3);

        assertFalse(resultado);
        assertEquals(3, chamadas[0]);
    }

    @Test
    void devePropagarOutrasExcecoes() {
        PagamentoService service = new PagamentoService(total -> {
            throw new RuntimeException("erro");
        });

        assertThrows(
                RuntimeException.class,
                () -> service.pagar(10_000, 3)
        );
    }

    @Test
    void deveRejeitarTotalZero() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.pagar(0, 1)
        );
    }

    @Test
    void deveRejeitarLimiteMenorQueUm() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.pagar(10_000, 0)
        );
    }

    @Test
    void deveRejeitarLimiteMaiorQueTres() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.pagar(10_000, 4)
        );
    }
}