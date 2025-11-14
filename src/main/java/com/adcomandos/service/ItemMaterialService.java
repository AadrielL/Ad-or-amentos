package com.adcomandos.service;

import com.adcomandos.dto.LevantamentoRequestDTO;
import com.adcomandos.model.ItemMaterial;
import com.adcomandos.model.TipoServico;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemMaterialService {

    // --- CONSTANTES DE CALCULO DE MATERIAL ---
    private static final double FATOR_CABO_POR_M2_ILUMINACAO = 1.0;
    private static final double FATOR_CABO_POR_M2_TUG = 2.0;

    private static final double COMPRIMENTO_CIRCUITO_DEDICADO = 25.0;

    // FATOR DE SEGURANÇA PARA ALIMENTAÇÃO PRINCIPAL
    private static final double MARGEM_PERCENTUAL_ALIMENTACAO = 1.2; // 20%
    private static final double ACRÉSCIMO_FIXO_ALIMENTACAO = 2.0; // 2 metros

    /**
     * Arredonda um valor Double para o número de casas decimais desejado.
     * @param value O valor a ser arredondado.
     * @param places O número de casas decimais.
     * @return O valor Double arredondado.
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("O número de casas decimais deve ser zero ou positivo.");

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Cria uma lista de itens materiais estimados com ajustes nas bitolas, quantidades e circuitos.
     * @param dto DTO contendo os dados do levantamento.
     * @return Lista de ItemMaterial com quantidades calculadas.
     */
    public List<ItemMaterial> criarItensMaterial(LevantamentoRequestDTO dto) {
        List<ItemMaterial> itens = new ArrayList<>();

        double m2 = dto.getMetragemQuadrada();

        // --- 1. CABOS E FIOS ---

        // 1.1. Iluminação (1.5mm²)
        double qtdFio1_5mm = round(m2 * FATOR_CABO_POR_M2_ILUMINACAO, 3);
        itens.add(new ItemMaterial("Fio 1.5mm² - Fase (PRETO) (Iluminação)", qtdFio1_5mm, "MT"));
        itens.add(new ItemMaterial("Fio 1.5mm² - Neutro (AZUL CLARO) (Iluminação)", qtdFio1_5mm, "MT"));
        itens.add(new ItemMaterial("Fio 1.5mm² - Retorno (AMARELO) (Iluminação)", qtdFio1_5mm, "MT"));

        // 1.2. Tomadas TUG (2.5mm²) - FASE e NEUTRO
        double qtdFio2_5mmBase = round(m2 * FATOR_CABO_POR_M2_TUG, 3);

        // FASE e NEUTRO TUG (2.5mm²)
        itens.add(new ItemMaterial("Fio 2.5mm² - Fase (VERMELHO) (Tomadas TUG)", qtdFio2_5mmBase, "MT"));
        itens.add(new ItemMaterial("Fio 2.5mm² - Neutro (AZUL CLARO) (Tomadas TUG)", qtdFio2_5mmBase, "MT"));

        // 1.3. Chuveiros e Ar Condicionado (4.0mm²) - FASE e NEUTRO (TUE)

        double qtdCircuitosDedicados = dto.getQuantidadeChuveiros() + dto.getQuantidadeArCondicionado();
        double comprimentoTotalDedicadoUnitario = qtdCircuitosDedicados * COMPRIMENTO_CIRCUITO_DEDICADO;

        double qtdFio4_0mmUnitario = round(comprimentoTotalDedicadoUnitario, 3);

        if (qtdFio4_0mmUnitario > 0) {
            // FASE (4.0mm²)
            itens.add(new ItemMaterial("Fio 4.0mm² - FASE Dedicado (Ex: Cinza/Branco) (TUE)", qtdFio4_0mmUnitario, "MT"));
            // NEUTRO (4.0mm²)
            itens.add(new ItemMaterial("Fio 4.0mm² - NEUTRO Dedicado (AZUL CLARO) (TUE)", qtdFio4_0mmUnitario, "MT"));
        }

        // 1.4. Fio Terra (PE) CONSOLIDADO (2.5mm² ou Bitola Equivalente)

        // Fio Terra TUG (2.5mm²)
        double qtdFio2_5mmTerraTUG = qtdFio2_5mmBase;

        // Fio Terra TUE (2.5mm²) - O Terra dos TUEs usa a bitola mínima de 2.5mm²
        double qtdFio2_5mmTerraTUE = round(comprimentoTotalDedicadoUnitario, 3);

        // CONSOLIDAÇÃO DO FIO TERRA 2.5MM² (TUG + TUE)
        double qtdFio2_5mmTerraTotal = qtdFio2_5mmTerraTUG + qtdFio2_5mmTerraTUE;

        itens.add(new ItemMaterial("Fio 2.5mm² - Terra (VERDE) (CONSOLIDADO TUG/TUE)", qtdFio2_5mmTerraTotal, "MT"));

        // 1.5. Alimentação Principal (Ramal de Entrada) - Fiação com cores e cálculo corrigido

        // Cálculo da metragem por condutor: Distância * 1.20 + 2.0 metros
        double comprimentoPorCondutor = (dto.getDistanciaPosteQuadro() * MARGEM_PERCENTUAL_ALIMENTACAO) + ACRÉSCIMO_FIXO_ALIMENTACAO;
        double qtdRamal = round(comprimentoPorCondutor, 3);

        if (qtdRamal > 0) {
            // FASE (10.0mm²)
            itens.add(new ItemMaterial("Fio 10.0mm² - FASE (PRETO) (Ramal de Entrada)", qtdRamal, "MT"));

            // NEUTRO (10.0mm²)
            itens.add(new ItemMaterial("Fio 10.0mm² - NEUTRO (AZUL) (Ramal de Entrada)", qtdRamal, "MT"));

            // TERRA (6.0mm²)
            itens.add(new ItemMaterial("Fio 6.0mm² - TERRA (VERDE) (Ramal de Entrada)", qtdRamal, "MT"));
        }


        // --- 2. CAIXAS DE DISPOSITIVO (REMOVIDO) ---


        // --- 3. DISJUNTORES (ESTIMATIVA DE CIRCUITOS) ---

        List<ItemMaterial> disjuntores = new ArrayList<>();

        // 3.1. Iluminação (Fixo: 1 Circuito)
        disjuntores.add(new ItemMaterial("Disjuntor Unipolar 10A (Iluminação)", 1.0, "UN"));

        // 3.2. TUG Quartos (Fixo: 1 Circuito)
        disjuntores.add(new ItemMaterial("Disjuntor Unipolar 16A (Tomadas Quartos)", 1.0, "UN"));

        // 3.3. TUG Sala/Cozinha (Fixo: 1 Circuito)
        disjuntores.add(new ItemMaterial("Disjuntor Unipolar 20A (Tomadas Sala/Cozinha)", 1.0, "UN"));

        // 3.4. TUEs (Chuveiros e Ar Condicionado) - Cada um é um circuito dedicado de 25A
        double qtdDisjuntores25A = dto.getQuantidadeChuveiros() + dto.getQuantidadeArCondicionado();

        if (qtdDisjuntores25A > 0) {
            // Assumimos que TUEs são Unipolares 25A
            disjuntores.add(new ItemMaterial("Disjuntor Unipolar 25A (TUEs - Chuveiro/Ar)", round(qtdDisjuntores25A, 0), "UN"));
        }

        // Adicionar todos os disjuntores calculados à lista principal de itens
        itens.addAll(disjuntores);

        // --- REMOVIDO: Quadro de Distribuição ---

        return itens;
    }
}