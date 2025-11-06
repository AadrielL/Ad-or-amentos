package com.adcomandos.service;

import com.adcomandos.dto.OrcamentoRequestDTO;
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.model.Material;
import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.MaterialRepository;
import com.adcomandos.repository.ServicoPadraoRepository;
import com.adcomandos.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrcamentoService {

    private final MaterialRepository materialRepository;
    private final ServicoPadraoRepository servicoPadraoRepository;
    private final OrcamentoRepository orcamentoRepository;

    // Taxas de Complexidade (Multiplicador de Lucro/Margem)
    private static final Map<String, BigDecimal> COMPLEXIDADE_MULTIPLIER = new HashMap<>();
    static {
        COMPLEXIDADE_MULTIPLIER.put("SIMPLES", new BigDecimal("1.25")); // 25% de margem
        COMPLEXIDADE_MULTIPLIER.put("MEDIO", new BigDecimal("1.40")); // 40%
        COMPLEXIDADE_MULTIPLIER.put("ALTA", new BigDecimal("1.60")); // 60%
    }

    private static final BigDecimal HORA_TECNICA_BASE = new BigDecimal("80.00");
    private static final BigDecimal HORA_AJUDANTE = new BigDecimal("35.00");

    public OrcamentoService(MaterialRepository materialRepository, ServicoPadraoRepository servicoPadraoRepository, OrcamentoRepository orcamentoRepository) {
        this.materialRepository = materialRepository;
        this.servicoPadraoRepository = servicoPadraoRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public OrcamentoResponseDTO calcularOrcamento(OrcamentoRequestDTO request, Usuario usuario) {

        BigDecimal custoMaterialTotal = BigDecimal.ZERO;
        BigDecimal custoMaoObraBase = BigDecimal.ZERO;

        for (OrcamentoRequestDTO.ItemOrcamentoDTO item : request.getItens()) {

            if ("MATERIAL".equalsIgnoreCase(item.getTipo())) {
                Material material = materialRepository.findById(item.getIdReferencia())
                        .orElseThrow(() -> new IllegalArgumentException("Material não encontrado: " + item.getIdReferencia()));

                if (!material.getUsuario().getId().equals(usuario.getId())) {
                    throw new SecurityException("Acesso negado ao material.");
                }

                BigDecimal custoItem = material.getPrecoCusto().multiply(BigDecimal.valueOf(item.getQuantidade()));
                custoMaterialTotal = custoMaterialTotal.add(custoItem);

            } else if ("SERVICO".equalsIgnoreCase(item.getTipo())) {
                ServicoPadrao servico = servicoPadraoRepository.findById(item.getIdReferencia())
                        .orElseThrow(() -> new IllegalArgumentException("Serviço Padrão não encontrado: " + item.getIdReferencia()));

                if (!servico.getUsuario().getId().equals(usuario.getId())) {
                    throw new SecurityException("Acesso negado ao serviço.");
                }

                BigDecimal custoServico = servico.getValorBase().multiply(BigDecimal.valueOf(item.getQuantidade()));
                custoMaoObraBase = custoMaoObraBase.add(custoServico);
            }
        }

        BigDecimal multiplicador = COMPLEXIDADE_MULTIPLIER.getOrDefault(
                request.getComplexidade().toUpperCase(),
                COMPLEXIDADE_MULTIPLIER.get("MEDIO")
        );

        BigDecimal valorMaterialFinal = custoMaterialTotal.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorMaoObraFinal = custoMaoObraBase.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorDeslocamento = BigDecimal.ZERO;
        if (request.getHorasDeslocamento() != null && request.getHorasDeslocamento() > 0) {
            BigDecimal horas = BigDecimal.valueOf(request.getHorasDeslocamento());
            valorDeslocamento = HORA_TECNICA_BASE.multiply(horas).multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorAjudante = BigDecimal.ZERO;
        if (request.getHorasAjudante() != null && request.getHorasAjudante() > 0) {
            BigDecimal horas = BigDecimal.valueOf(request.getHorasAjudante());
            valorAjudante = HORA_AJUDANTE.multiply(horas).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorTotal = valorMaterialFinal
                .add(valorMaoObraFinal)
                .add(valorDeslocamento)
                .add(valorAjudante)
                .setScale(2, RoundingMode.HALF_UP);

        // TODO: A lógica para mapear e salvar o objeto Orcamento no banco (usando orcamentoRepository) viria aqui.

        return OrcamentoResponseDTO.builder()
                .clienteNome(request.getClienteNome())
                .valorMaterial(valorMaterialFinal)
                .valorMaoObra(valorMaoObraFinal)
                .valorDeslocamento(valorDeslocamento)
                .valorAjudante(valorAjudante)
                .valorTotal(valorTotal)
                .complexidadeAplicada(request.getComplexidade().toUpperCase())
                .mensagem("Orçamento calculado com sucesso!")
                .build();
    }
}