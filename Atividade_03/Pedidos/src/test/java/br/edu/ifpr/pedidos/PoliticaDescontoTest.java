package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {

    @Test
    void deveDar10PorcentoParaVip() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 0);

        assertEquals(1_000, politica.calcular(cliente, 10_000, null));
    }

    @Test
    void deveDar5PorcentoParaClienteComumAcimaDe500() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_500, politica.calcular(cliente, 50_000, null));
    }

    @Test
    void deveDarZeroParaClienteComumAbaixoDe500() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, politica.calcular(cliente, 49_999, null));
    }

    @Test
    void deveAplicarCupomBemVindo() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_000, politica.calcular(cliente, 10_000, "BEMVINDO"));
    }

    @Test
    void naoDeveAplicarBemVindoComComprasAnteriores() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 1);

        assertEquals(0, politica.calcular(cliente, 10_000, "BEMVINDO"));
    }

    @Test
    void naoDeveAplicarBemVindoAbaixoDe100() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, politica.calcular(cliente, 9_999, "BEMVINDO"));
    }

    @Test
    void deveAplicarCupomExtra10() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_000, politica.calcular(cliente, 20_000, "EXTRA10"));
    }

    @Test
    void naoDeveAplicarExtra10AbaixoDe200() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, politica.calcular(cliente, 19_999, "EXTRA10"));
    }

    @Test
    void deveAceitarCupomComEspacosEMinusculas() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_000, politica.calcular(cliente, 20_000, " extra10 "));
    }

    @Test
    void deveLancarExcecaoParaCupomDesconhecido() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> politica.calcular(cliente, 10_000, "INVALIDO")
        );
    }

    @Test
    void deveLimitarDescontoA20Porcento() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 0);

        assertEquals(4_000, politica.calcular(cliente, 20_000, "EXTRA10"));
    }

    @Test
    void deveManterDescontoQuandoCupomForNulo() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_500, politica.calcular(cliente, 50_000, null));
    }

    @Test
    void deveManterDescontoQuandoCupomForBranco() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_500, politica.calcular(cliente, 50_000, "   "));
    }
    @Test
    void deveLimitarDescontoQuandoUltrapassar20Porcento() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(true, false, 0);

        long desconto = politica.calcular(cliente, 10_000, "BEMVINDO");

        assertEquals(2_000, desconto);
    }
    @Test
    void deveLancarExcecaoParaSubtotalNegativo() {
        PoliticaDesconto politica = new PoliticaDesconto();
        Cliente cliente = new Cliente(false, false, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> politica.calcular(cliente, -1, null)
        );
    }}
