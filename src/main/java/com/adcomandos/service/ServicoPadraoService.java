package com.adcomandos.service;

import com.adcomandos.dto.ServicoPadraoRequestDTO;
import com.adcomandos.model.ServicoPadrao;
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.ServicoPadraoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ServicoPadraoService {

    private final ServicoPadraoRepository servicoPadraoRepository;

    public ServicoPadraoService(ServicoPadraoRepository servicoPadraoRepository) {
        this.servicoPadraoRepository = servicoPadraoRepository;
    }

    /**
     * 1. CREATE: Cadastra um novo serviço padrão para o usuário logado.
     */
    @Transactional
    public ServicoPadrao create(ServicoPadraoRequestDTO dto, Usuario usuario) {
        ServicoPadrao servico = new ServicoPadrao();
        servico.setNome(dto.getNome());
        servico.setTipo(dto.getTipo());
        servico.setValorBase(dto.getValorBase());
        servico.setTempoEstimadoHoras(dto.getTempoEstimadoHoras());
        // Associa o serviço ao usuário logado (isolamento de dados)
        servico.setUsuario(usuario);

        return servicoPadraoRepository.save(servico);
    }

    /**
     * 2. READ: Busca todos os serviços que pertencem ao usuário logado.
     */
    public List<ServicoPadrao> findAllByUsuario(Usuario usuario) {
        // Usa o método customizado do JpaRepository
        return servicoPadraoRepository.findByUsuario(usuario);
    }

    /**
     * 3. UPDATE: Atualiza um serviço existente, garantindo que pertença ao usuário logado.
     */
    @Transactional
    public ServicoPadrao update(Long id, ServicoPadraoRequestDTO dto, Usuario usuario) {
        ServicoPadrao servico = servicoPadraoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço Padrão não encontrado."));

        // ⚠️ Validação de Propriedade
        if (!servico.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("Acesso negado. Você não tem permissão para alterar este serviço.");
        }

        servico.setNome(dto.getNome());
        servico.setTipo(dto.getTipo());
        servico.setValorBase(dto.getValorBase());
        servico.setTempoEstimadoHoras(dto.getTempoEstimadoHoras());

        return servicoPadraoRepository.save(servico);
    }

    /**
     * 4. DELETE: Deleta um serviço existente, garantindo que pertença ao usuário logado.
     */
    @Transactional
    public void delete(Long id, Usuario usuario) {
        ServicoPadrao servico = servicoPadraoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço Padrão não encontrado."));

        // ⚠️ Validação de Propriedade
        if (!servico.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("Acesso negado. Você não tem permissão para deletar este serviço.");
        }

        servicoPadraoRepository.delete(servico);
    }
}