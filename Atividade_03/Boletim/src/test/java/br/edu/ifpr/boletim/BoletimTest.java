package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveAprovarAlunoComMediaOito() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(8);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaQuatro() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(4);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(2);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaIgualCinco() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(5,5);

        assertEquals(5,resultado);

    }
    @Test
    void deveAprovarAlunoComMedia7OuMaior() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(7);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveColocarAlunoEmRecuperacaoComMediaEntre4E6() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(6);

        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaMenorQue4() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(3);

        assertEquals("REPROVADO", resultado);
    }
    @Test
    void deveCalcularMedia() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(8, 7);

        assertEquals(7.5, resultado, 0.0001);
    }
    @Test
    void deveRetornarZeroQuandoArrayEstiverVazio() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {});

        assertEquals(0, resultado);
    }
    @Test
    void deveContarUmAlunoAprovado() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {8});

        assertEquals(1, resultado);
    }
    @Test
    void deveContarApenasOsAlunosAprovados() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(
                new double[] {8, 5, 7, 3}
        );

        assertEquals(2, resultado);
    }
}
