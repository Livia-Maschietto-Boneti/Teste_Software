package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {

    private Pedido pedido(String uf, boolean expresso, boolean fragil, int pesoGramas) {
        ItemPedido item = new ItemPedido(
                "SKU-1",
                10_000,
                1,
                10,
                pesoGramas,
                fragil
        );

        return new Pedido(List.of(item), uf, expresso, null);
    }

    @Test
    void deveCobrarFreteBaseParaPR() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(1_200, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveCobrarFreteBaseParaSPEJR() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(
                2_000,
                calculadora.calcular(
                        pedido("SP", false, false, 1_000),
                        cliente,
                        10_000
                )
        );

        assertEquals(
                2_000,
                calculadora.calcular(
                        pedido("RJ", false, false, 1_000),
                        cliente,
                        10_000
                )
        );
    }

    @Test
    void deveCobrarFretePadraoParaOutrasUFs() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("MG", false, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(3_000, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveExecutarUmaIteracaoDoPesoExcedente() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 3_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(1_500, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveExecutarVariasIteracoesDoPesoExcedente() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 5_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_100, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void naoDeveExecutarLacoQuandoPesoForAteDoisQuilos() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 2_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(1_200, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveZerarFreteQuandoLiquidoForMaiorOuIgualA300() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, calculadora.calcular(pedido, cliente, 30_000));
    }

    @Test
    void naoDeveZerarFreteQuandoEntregaForExpressa() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", true, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_700, calculadora.calcular(pedido, cliente, 30_000));
    }

    @Test
    void deveCobrarMetadeParaClienteVip() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 1_000);
        Cliente cliente = new Cliente(true, false, 0);

        assertEquals(600, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveAdicionarTaxaDeEntregaExpressa() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", true, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(2_700, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveAdicionarTaxaDeItemFragil() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, true, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(1_700, calculadora.calcular(pedido, cliente, 10_000));
    }

    @Test
    void deveAdicionarTaxaExpressaEFragilMesmoComFreteZerado() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", true, true, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(3_200, calculadora.calcular(pedido, cliente, 30_000));
    }

    @Test
    void deveRejeitarLiquidoNegativo() {
        CalculadoraFrete calculadora = new CalculadoraFrete();
        Pedido pedido = pedido("PR", false, false, 1_000);
        Cliente cliente = new Cliente(false, false, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.calcular(pedido, cliente, -1)
        );
    }
}