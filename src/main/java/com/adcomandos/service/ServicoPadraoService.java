package com.adcomandos.service;

import com.adcomandos.dto.ServicoPadraoRequestDTO;
import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.ServicoPadraoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Lógica de mapeamento restaurada para a forma simples (sem regras).
@Service
public class ServicoPadraoService {

    private final ServicoPadraoRepository servicoPadraoRepository;

    public ServicoPadraoService(ServicoPadraoRepository servicoPadraoRepository) {
        this.servicoPadraoRepository = servicoPadraoRepository;
    }

    /**
     * Cria um novo serviço padrão e o associa ao usuário logado.
     */
    public ServicoPadrao create(ServicoPadraoRequestDTO dto, Usuario usuarioLogado) {
        ServicoPadrao servico = new ServicoPadrao();

        // Mapeamento dos dados do DTO para a Entidade (SIMPLIFICADO)
        servico.setNome(dto.getNome());
        servico.setTipo(dto.getTipo());
        servico.setValorBase(dto.getValorBase());
        servico.setTempoEstimadoHoras(dto.getTempoEstimadoHoras());

        // Associação essencial: define o proprietário do serviço
        servico.setUsuario(usuarioLogado);

        return servicoPadraoRepository.save(servico);
    }

    /**
     * Retorna todos os serviços padrão criados por um usuário específico.
     */
    public List<ServicoPadrao> findAllByUsuario(Usuario usuarioLogado) {
        return servicoPadraoRepository.findAllByUsuario(usuarioLogado);
    }

    /**
     * Atualiza um serviço, garantindo que ele pertença ao usuário logado.
     */
    public ServicoPadrao update(Long id, ServicoPadraoRequestDTO dto, Usuario usuarioLogado) {
        ServicoPadrao servico = findAndValidate(id, usuarioLogado);

        // Atualiza os campos
        servico.setNome(dto.getNome());
        servico.setTipo(dto.getTipo());
        servico.setValorBase(dto.getValorBase());
        servico.setTempoEstimadoHoras(dto.getTempoEstimadoHoras());

        return servicoPadraoRepository.save(servico);
    }

    /**
     * Deleta um serviço, garantindo que ele pertença ao usuário logado.
     */
    public void delete(Long id, Usuario usuarioLogado) {
        ServicoPadrao servico = findAndValidate(id, usuarioLogado);
        servicoPadraoRepository.delete(servico);
    }

    // --- Métodos de Validação ---

    /**
     * Busca um serviço pelo ID e valida se ele pertence ao usuário logado.
     * @throws IllegalArgumentException se o serviço não for encontrado (404)
     * @throws SecurityException se o serviço não pertencer ao usuário (403)
     */
    private ServicoPadrao findAndValidate(Long id, Usuario usuarioLogado) {
        ServicoPadrao servico = servicoPadraoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + id));

        if (!servico.getUsuario().getId().equals(usuarioLogado.getId())) {
            // Lança SecurityException, que é capturada pelo Controller para retornar 403 Forbidden
            throw new SecurityException("Acesso negado. O serviço não pertence ao usuário logado.");
        }
        return servico;
    }
}