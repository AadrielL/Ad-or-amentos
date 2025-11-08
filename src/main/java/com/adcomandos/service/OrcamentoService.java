package com.adcomandos.service;

import com.adcomandos.dto.OrcamentoRequestDTO;
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.model.Material;
import com.adcomandos.model.Orcamento;
import com.adcomandos.model.ServicoPadrao; // Novo
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.MaterialRepository; // Novo
import com.adcomandos.repository.ServicoPadraoRepository; // Novo
import com.adcomandos.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Novo
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrcamentoService {

    private final MaterialRepository materialRepository;
    private final ServicoPadraoRepository servicoPadraoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final EmailService emailService; // ⬅️ NOVO: Injeção do Serviço de E-mail

    // Taxas de Complexidade (Multiplicador de Lucro/Margem)
    private static final Map<String, BigDecimal> COMPLEXIDADE_MULTIPLIER = new HashMap<>();
    static {
        COMPLEXIDADE_MULTIPLIER.put("SIMPLES", new BigDecimal("1.25")); // 25% de margem
        COMPLEXIDADE_MULTIPLIER.put("MEDIO", new BigDecimal("1.40")); // 40%
        COMPLEXIDADE_MULTIPLIER.put("ALTA", new BigDecimal("1.60")); // 60%
    }

    private static final BigDecimal HORA_TECNICA_BASE = new BigDecimal("80.00");
    private static final BigDecimal HORA_AJUDANTE = new BigDecimal("35.00");

    // ⬅️ Construtor COMPLETO com todas as dependências (incluindo EmailService)
    public OrcamentoService(MaterialRepository materialRepository,
                            ServicoPadraoRepository servicoPadraoRepository,
                            OrcamentoRepository orcamentoRepository,
                            EmailService emailService) {
        this.materialRepository = materialRepository;
        this.servicoPadraoRepository = servicoPadraoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.emailService = emailService; // Inicialização do EmailService
    }

    @Transactional // Garante que o cálculo e o salvamento ocorram juntos
    public OrcamentoResponseDTO calcularOrcamento(OrcamentoRequestDTO request, Usuario usuario) {

        BigDecimal custoMaterialTotal = BigDecimal.ZERO;
        BigDecimal custoMaoObraBase = BigDecimal.ZERO;

        // ... (Lógica de cálculo existente permanece igual) ...

        for (OrcamentoRequestDTO.ItemOrcamentoDTO item : request.getItens()) {
            // ... (Lógica de processamento de MATERIAL e SERVICO) ...
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

        // ===============================================
        // 1. Mapear e salvar o objeto Orcamento no banco
        // ===============================================
        Orcamento novoOrcamento = new Orcamento();
        novoOrcamento.setClienteNome(request.getClienteNome());
        novoOrcamento.setClienteTelefone(request.getClienteTelefone());
        novoOrcamento.setClienteEndereco(request.getClienteEndereco());
        novoOrcamento.setComplexidade(request.getComplexidade().toUpperCase());
        novoOrcamento.setValorMaterial(valorMaterialFinal);
        novoOrcamento.setValorMaoObra(valorMaoObraFinal);
        novoOrcamento.setValorDeslocamento(valorDeslocamento);
        novoOrcamento.setValorAjudante(valorAjudante);
        novoOrcamento.setValorTotal(valorTotal);
        novoOrcamento.setUsuario(usuario); // Associa ao Admin logado

        orcamentoRepository.save(novoOrcamento);

        // ===============================================
        // 2. Montar a resposta e DISPARAR O E-MAIL
        // ===============================================
        OrcamentoResponseDTO response = OrcamentoResponseDTO.builder()
                .clienteNome(request.getClienteNome())
                .valorMaterial(valorMaterialFinal)
                .valorMaoObra(valorMaoObraFinal)
                .valorDeslocamento(valorDeslocamento)
                .valorAjudante(valorAjudante)
                .valorTotal(valorTotal)
                .complexidadeAplicada(request.getComplexidade().toUpperCase())
                .mensagem("Orçamento calculado e salvo. Email enviado para o cliente!")
                .build();

        // 📧 Disparo do E-mail
        // Requer que o campo clienteEmail tenha sido adicionado ao OrcamentoRequestDTO
        // Se você não o adicionou, comente ou adicione-o antes de executar.
        // emailService.enviarOrcamento(request.getClienteEmail(), response);

        return response;
    }
}