package com.adcomandos.service;

import com.adcomandos.dto.RegistroAdminRequestDTO;
import com.adcomandos.model.CodigoAdmin;
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.CodigoAdminRepository;
import com.adcomandos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final CodigoAdminRepository codigoAdminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UsuarioRepository usuarioRepository,
                        CodigoAdminRepository codigoAdminRepository,
                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.codigoAdminRepository = codigoAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário com a role ADMIN, consumindo um código de ativação.
     */
    @Transactional
    public Usuario registrarAdmin(RegistroAdminRequestDTO request) {

        // 1. Verifica se o e-mail já está em uso
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está registrado.");
        }

        // 2. Valida o Código de Ativação
        CodigoAdmin codigoAtivacao = codigoAdminRepository
                .findByCodigoAndStatus(request.getCodigoAtivacao(), CodigoAdmin.Status.ATIVO)
                .orElseThrow(() -> new IllegalArgumentException("Código de ativação inválido ou inativo."));

        // 3. Cria e Salva o novo Usuário
        Usuario novoAdmin = new Usuario();
        novoAdmin.setEmail(request.getEmail());
        novoAdmin.setNome(request.getNome());
        novoAdmin.setSenha(passwordEncoder.encode(request.getSenha()));
        novoAdmin.setRole("ADMIN"); // Define explicitamente a role

        Usuario adminSalvo = usuarioRepository.save(novoAdmin);

        // 4. Invalida o Código de Ativação para uso único
        codigoAtivacao.setStatus(CodigoAdmin.Status.USADO);
        codigoAtivacao.setUsuario(adminSalvo); // Associa o código ao Admin criado
        codigoAdminRepository.save(codigoAtivacao);

        return adminSalvo;
    }
}