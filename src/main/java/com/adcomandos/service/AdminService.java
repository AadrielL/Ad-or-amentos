package com.adcomandos.service;

import com.adcomandos.model.CodigoAdmin;
import com.adcomandos.model.Usuario;
import com.adcomandos.repository.CodigoAdminRepository;
import com.adcomandos.repository.UsuarioRepository;
import com.adcomandos.dto.AdminCreationDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final CodigoAdminRepository codigoAdminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UsuarioRepository usuarioRepository, CodigoAdminRepository codigoAdminRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.codigoAdminRepository = codigoAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criarPrimeiroAdmin(AdminCreationDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        CodigoAdmin codigoValidado = codigoAdminRepository
                .findByCodigoAndStatus(dto.getCodigoAtivacao(), "VALIDO")
                .orElseThrow(() -> new IllegalArgumentException("Código de ativação inválido ou já utilizado."));

        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome(dto.getNome());
        novoAdmin.setEmail(dto.getEmail());

        novoAdmin.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoAdmin.setRole("ADMIN");

        Usuario adminSalvo = usuarioRepository.save(novoAdmin);

        codigoValidado.setStatus("USADO");
        codigoValidado.setUsuario(adminSalvo);
        codigoAdminRepository.save(codigoValidado);

        return adminSalvo;
    }
}