package com.adcomandos.service;

import com.adcomandos.dto.LevantamentoRequestDTO;
import com.adcomandos.dto.OrcamentoResponseDTO;
import com.adcomandos.model.Orcamento;
import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import com.adcomandos.model.ItemMaterial;
import com.adcomandos.repository.OrcamentoRepository;
import com.adcomandos.repository.ServicoPadraoRepository;
import com.adcomandos.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemMaterialService itemMaterialService;
    private final ServicoPadraoRepository servicoPadraoRepository;

    private static final Long ID_USUARIO_PADRAO = 1L;

    // --- CONSTANTES DE CÁLCULO DE CUSTO (AJUSTAR!) ---
    private static final double VALOR_BASE_POR_M2 = 12.00;     // Exemplo: R$ 12,00 por m²
    private static final int LIMITE_PONTOS_PADRAO = 35;       // Limite de pontos incluídos no valor base
    private static final double VALOR_ADICIONAL_POR_PONTO = 25.00; // Custo por ponto extra

    // --- NOVAS CONSTANTES DE CÁLCULO DE TEMPO (AJUSTAR!) ---
    private static final double TEMPO_BASE_SETUP_HORAS = 4.0;   // Tempo fixo para deslocamento, organização, etc.
    private static final double TEMPO_POR_PONTO_HORAS = 0.5;    // Tempo médio por tomada/ponto de luz (30 min)
    private static final double TEMPO_POR_CHUVEIRO_HORAS = 1.0; // Tempo adicional por chuveiro
    private static final double TEMPO_POR_AR_CONDICIONADO_HORAS = 1.5; // Tempo adicional por AC

    // Configurações de custo fixo e margem
    private static final BigDecimal FATOR_COMPLEXIDADE = new BigDecimal("1.2"); // Margem de lucro (20%)
    private static final double VALOR_AJUDANTE = 150.00;
    private static final double VALOR_DESLOCAMENTO = 50.00;


    public OrcamentoService(OrcamentoRepository orcamentoRepository,
                            UsuarioRepository usuarioRepository,
                            ItemMaterialService itemMaterialService,
                            ServicoPadraoRepository servicoPadraoRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemMaterialService = itemMaterialService;
        this.servicoPadraoRepository = servicoPadraoRepository;
    }

    @Transactional
    public OrcamentoResponseDTO calcularOrcamentoParaCliente(LevantamentoRequestDTO request) {

        Usuario usuarioPadrao = usuarioRepository.findById(ID_USUARIO_PADRAO)
                .orElseThrow(() -> new NoSuchElementException("Usuário padrão (ID " + ID_USUARIO_PADRAO + ") para clientes anônimos não encontrado!"));

        // 1. Levantamento de Material
        List<ItemMaterial> itensMaterial = itemMaterialService.criarItensMaterial(request);

        // 2. CÁLCULO DE CUSTOS DE SERVIÇO (M² + Pontos Extras)
        BigDecimal custoMaoDeObra = BigDecimal.valueOf(calcularCustoMaoDeObra(request)).setScale(2, RoundingMode.HALF_UP);

        // 3. CÁLCULO DO TEMPO ESTIMADO (Baseado nos Pontos e Equipamentos)
        Double tempoEstimado = calcularTempoEstimado(request);

        // Conversão dos valores fixos para BigDecimal para cálculos
        BigDecimal valorAjudante = BigDecimal.valueOf(VALOR_AJUDANTE);
        BigDecimal valorDeslocamento = BigDecimal.valueOf(VALOR_DESLOCAMENTO);

        // 4. CÁLCULO DO VALOR TOTAL (Serviço + Custos Fixos + Margem)
        BigDecimal custoMaterial = BigDecimal.ZERO;

        BigDecimal subTotalServico = custoMaoDeObra
                .add(valorAjudante)
                .add(valorDeslocamento);

        // Aplica a margem de lucro/complexidade
        BigDecimal valorTotalServico = subTotalServico
                .multiply(FATOR_COMPLEXIDADE)
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Persistência e Resposta
        Orcamento orcamentoSalvo = salvarOrcamento(
                request,
                custoMaoDeObra,
                valorAjudante,
                valorDeslocamento,
                tempoEstimado,
                valorTotalServico,
                usuarioPadrao,
                itensMaterial
        );

        // Mapeamento para DTO de resposta
        return OrcamentoResponseDTO.builder()
                .id(orcamentoSalvo.getId())
                .clienteNome(orcamentoSalvo.getClienteNome())
                .endereco(orcamentoSalvo.getEndereco())
                .dataGeracao(orcamentoSalvo.getDataGeracao())
                .adminResponsavel(usuarioPadrao.getEmail())
                .nivelComplexidade(orcamentoSalvo.getNivelComplexidade())
                .custoTotalMaoDeObra(custoMaoDeObra)
                .valorAjudante(valorAjudante)
                .valorDeslocamento(valorDeslocamento)
                .custoTotalMaterial(custoMaterial)
                .valorTotalServico(valorTotalServico)
                .tempoEstimadoHoras(tempoEstimado)
                .materiaisRecomendados(mapItensMaterialToDTO(itensMaterial))
                .build();
    }

    // --- MÉTODOS DE LÓGICA DE NEGÓCIO ---

    /**
     * Implementa a regra: Custo Base (M²) + Adicional para Pontos Excedentes ( > 35).
     */
    private double calcularCustoMaoDeObra(LevantamentoRequestDTO dto) {
        double custoBaseM2 = dto.getMetragemQuadrada() * VALOR_BASE_POR_M2;
        int totalPontos = dto.getQuantidadeTomadas() + dto.getQuantidadePontosLuz();

        double custoAdicionalPontos = 0.0;

        if (totalPontos > LIMITE_PONTOS_PADRAO) {
            int pontosExtras = totalPontos - LIMITE_PONTOS_PADRAO;
            custoAdicionalPontos = pontosExtras * VALOR_ADICIONAL_POR_PONTO;
        }

        return custoBaseM2 + custoAdicionalPontos;
    }

    /**
     * Implementa a regra de cálculo de tempo baseado em pontos e equipamentos.
     */
    private Double calcularTempoEstimado(LevantamentoRequestDTO dto) {
        // Cálculo do total de pontos (tomadas + luzes)
        double totalPontos = dto.getQuantidadeTomadas() + dto.getQuantidadePontosLuz();

        double tempoPontos = totalPontos * TEMPO_POR_PONTO_HORAS;
        double tempoChuveiros = dto.getQuantidadeChuveiros() * TEMPO_POR_CHUVEIRO_HORAS;
        double tempoArCondicionado = dto.getQuantidadeArCondicionado() * TEMPO_POR_AR_CONDICIONADO_HORAS;

        double tempoTotal = TEMPO_BASE_SETUP_HORAS + tempoPontos + tempoChuveiros + tempoArCondicionado;

        // Arredonda para 1 casa decimal
        return BigDecimal.valueOf(tempoTotal)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Busca a primeira regra de Serviço Padrão disponível (Ainda usada para validar se há admin cadastrado).
     */
    private ServicoPadrao buscarRegraServicoSimplificada() {
        return servicoPadraoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhum ServicoPadrao encontrado. Cadastre um serviço como ADMIN."));
    }

    private Orcamento salvarOrcamento(LevantamentoRequestDTO request,
                                      BigDecimal custoMaoDeObra,
                                      BigDecimal valorAjudante,
                                      BigDecimal valorDeslocamento,
                                      Double tempoEstimado,
                                      BigDecimal valorTotalServico,
                                      Usuario admin,
                                      List<ItemMaterial> itensMaterial) {
        Orcamento orcamento = new Orcamento();
        orcamento.setClienteNome(request.getClienteNome() != null ? request.getClienteNome() : "Cliente Anônimo");
        orcamento.setEndereco(request.getEndereco() != null ? request.getEndereco() : "Endereço Não Informado");
        orcamento.setClienteTelefone(request.getClienteTelefone());
        orcamento.setMetragemQuadrada(request.getMetragemQuadrada());
        orcamento.setQuantidadeTomadas(request.getQuantidadeTomadas());
        orcamento.setQuantidadePontosLuz(request.getQuantidadePontosLuz());
        orcamento.setCustoTotalMaoDeObra(custoMaoDeObra);
        orcamento.setValorAjudante(valorAjudante);
        orcamento.setValorDeslocamento(valorDeslocamento);
        orcamento.setTempoEstimadoHoras(tempoEstimado);
        orcamento.setNivelComplexidade(request.getNivelComplexidade());
        orcamento.setValorTotalServico(valorTotalServico);
        orcamento.setUsuario(admin);

        if (itensMaterial != null) {
            orcamento.setItensMaterial(itensMaterial);
            itensMaterial.forEach(item -> item.setOrcamento(orcamento));
        }

        return orcamentoRepository.save(orcamento);
    }

    private List<OrcamentoResponseDTO.MaterialRecomendadoDTO> mapItensMaterialToDTO(List<ItemMaterial> itens) {
        if (itens == null) {
            return Collections.emptyList();
        }
        return itens.stream()
                .map(item -> OrcamentoResponseDTO.MaterialRecomendadoDTO.builder()
                        .nome(item.getNomeMaterial())
                        .quantidadeNecessaria(item.getQuantidadeNecessaria())
                        .unidadeMedida(item.getUnidadeMedida())
                        .custoUnitarioReferencia(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }

    }
