package com.adcomandos.util;

import java.util.List;
import java.util.NoSuchElementException;

public class DimensionamentoUtil {

    // Constante de Resistividade do Cobre a 20°C (Ohm*mm²/m)
    private static final double RHO_COBRE = 0.0172;

    // Limite máximo de Queda de Tensão (3% da tensão nominal)
    private static final double TENSAO_NOMINAL = 220.0;
    private static final double QUEDA_TENSAO_MAX_PERCENTUAL = 0.03;
    private static final double QUEDA_TENSAO_MAX_VOLTS = TENSAO_NOMINAL * QUEDA_TENSAO_MAX_PERCENTUAL;

    // Tabela de Capacidade de Corrente (Simplificada de NBR 5410/Tabela 36 - Método de Referência B1)
    private static final List<BitolaCorrente> TABELA_CAPACIDADE_CORRENTE = List.of(
            new BitolaCorrente(1.5, 15.5),
            new BitolaCorrente(2.5, 21.0),
            new BitolaCorrente(4.0, 28.0),
            new BitolaCorrente(6.0, 36.0),
            new BitolaCorrente(10.0, 50.0),
            new BitolaCorrente(16.0, 68.0),
            new BitolaCorrente(25.0, 89.0)
    );

    private record BitolaCorrente(double bitolaMM2, double capacidadeAmperes) {}

    /**
     * Calcula a bitola mínima necessária com base em DOIS CRITÉRIOS: Corrente e Queda de Tensão.
     */
    public static double calcularBitolaOtimizada(double potenciaTotalWatts, double distanciaMetros, double tensaoVolts, double fatorPotencia) {

        // 1. CÁLCULO DA CORRENTE DE PROJETO (I)
        double correnteProjeto = potenciaTotalWatts / (tensaoVolts * fatorPotencia);

        // 2. DIMENSIONAMENTO PELA CAPACIDADE DE CORRENTE (PROTEÇÃO TÉRMICA)
        double bitolaCorrente = TABELA_CAPACIDADE_CORRENTE.stream()
                .filter(item -> item.capacidadeAmperes() >= correnteProjeto)
                .findFirst()
                .map(BitolaCorrente::bitolaMM2)
                .orElseThrow(() -> new NoSuchElementException("Corrente muito alta. Não há bitola na tabela que suporte " + correnteProjeto + "A."));

        // 3. DIMENSIONAMENTO PELA QUEDA DE TENSÃO (A/mm²)
        // A = (2 * ρ * L * I) / ΔV_max
        double bitolaQuedaTensao = (2 * RHO_COBRE * distanciaMetros * correnteProjeto) / QUEDA_TENSAO_MAX_VOLTS;

        // 4. ESCOLHA DA MAIOR BITOLA
        double bitolaMinima = Math.max(bitolaCorrente, bitolaQuedaTensao);

        // 5. SELEÇÃO DA PRÓXIMA BITOLA COMERCIAL DISPONÍVEL
        return TABELA_CAPACIDADE_CORRENTE.stream()
                .filter(item -> item.bitolaMM2() >= bitolaMinima)
                .findFirst()
                .map(BitolaCorrente::bitolaMM2)
                .orElse(TABELA_CAPACIDADE_CORRENTE.get(TABELA_CAPACIDADE_CORRENTE.size() - 1).bitolaMM2());
    }
}