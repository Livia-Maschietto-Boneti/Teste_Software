package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteValido() {
        Cliente cliente = new Cliente(true, false, 5);

        assertTrue(cliente.vip());
        assertFalse(cliente.bloqueado());
        assertEquals(5, cliente.comprasAnteriores());
    }

    @Test
    void devePermitirClienteComZeroComprasAnteriores() {
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, cliente.comprasAnteriores());
    }

    @Test
    void devePermitirClienteBloqueado() {
        Cliente cliente = new Cliente(false, true, 3);

        assertTrue(cliente.bloqueado());
    }

    @Test
    void deveLancarExcecaoParaHistoricoNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Cliente(false, false, -1)
        );
    }
}