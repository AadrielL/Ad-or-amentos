package com.adcomandos.service;

import com.adcomandos.dto.LevantamentoRequestDTO; // Usando o novo DTO
import com.adcomandos.dto.OrcamentoResponseDTO; // Usando o DTO de Resposta existente
import com.adcomandos.model.ItemMaterial; 
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevantamentoMaterialService {

    private final ItemMaterialService itemMaterialService;

    public LevantamentoMaterialService(ItemMaterialService itemMaterialService) {
        this.itemMaterialService = itemMaterialService;
    }

    /**
     * Levanta o material (Simulação/Cálculo Técnico) com base nas regras do ItemMaterialService.
     * Não salva no DB, não calcula custos de serviço/tempo.
     * @param request DTO com dados técnicos.
     * @param emailAdmin Email do admin.
     * @return DTO de resposta contendo apenas a lista de materiais e campos zerados.
     */
    public OrcamentoResponseDTO levantarMaterial(LevantamentoRequestDTO request, String emailAdmin) {

        // O ItemMaterialService precisa ser ajustado para aceitar o LevantamentoRequestDTO
        // Como a estrutura de dados técnicos é a mesma, podemos fazer um truque ou adaptar.
        // Por agora, vamos assumir que o ItemMaterialService foi adaptado para o novo DTO.
        
        // CORREÇÃO NECESSÁRIA: O ItemMaterialService.criarItensMaterial precisa ser atualizado
        // para aceitar LevantamentoRequestDTO. Vou manter a chamada e você deve fazer o ajuste lá.
        
        List<ItemMaterial> itensMaterial = itemMaterialService.criarItensMaterial(request); 

        List<OrcamentoResponseDTO.MaterialRecomendadoDTO> materiaisParaDTO =
                itensMaterial.stream()
                        .map(item -> OrcamentoResponseDTO.MaterialRecomendadoDTO.builder()
                                .nome(item.getNomeMaterial())
                                .quantidadeNecessaria(item.getQuantidadeNecessaria())
                                .unidadeMedida(item.getUnidadeMedida())
                                .custoUnitarioReferencia(item.getCustoUnitarioReferencia())
                                .build())
                        .collect(Collectors.toList());

        // Retorna um DTO SIMPLIFICADO: apenas os materiais e zeros nos campos de valor/tempo
        return OrcamentoResponseDTO.builder()
                .materiaisRecomendados(materiaisParaDTO)
                .custoTotalMaoDeObra(BigDecimal.ZERO)
                .custoTotalMaterial(BigDecimal.ZERO)
                .valorTotalServico(BigDecimal.ZERO)
                .tempoEstimadoHoras(0.0)
                .adminResponsavel(emailAdmin)
                .clienteNome("Não Aplicável")
                .endereco("Não Aplicável")
                .dataGeracao(null)
                .id(0L) 
                .build();
    }
}