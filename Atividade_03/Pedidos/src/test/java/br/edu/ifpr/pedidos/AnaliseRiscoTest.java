package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {

    @Test
    void deveRecusarClienteBloqueado() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, true, 0);

        String resultado = risco.avaliar(cliente, 50_000, false);

        assertEquals("RECUSADO", resultado);
    }

    @Test
    void deveLancarExcecaoParaTotalNegativo() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> risco.avaliar(cliente, -1, false)
        );
    }

    @Test
    void deveColocarEmRevisaoClienteSemComprasComTotalAlto() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 100_001, false);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveColocarEmRevisaoClienteSemComprasComEntregaExpressa() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 50_000, true);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveAprovarClienteSemComprasComTotalBaixoEEntregaNormal() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 0);

        String resultado = risco.avaliar(cliente, 50_000, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveColocarEmRevisaoClienteComComprasNaoVipETotalAlto() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 500_001, false);

        assertEquals("REVISAO", resultado);
    }

    @Test
    void deveAprovarClienteVipMesmoComTotalAlto() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(true, false, 1);

        String resultado = risco.avaliar(cliente, 500_001, false);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveAprovarClienteComComprasComTotalAte500000() {
        AnaliseRisco risco = new AnaliseRisco();
        Cliente cliente = new Cliente(false, false, 1);

        String resultado = risco.avaliar(cliente, 500_000, false);

        assertEquals("APROVADO", resultado);
    }
}