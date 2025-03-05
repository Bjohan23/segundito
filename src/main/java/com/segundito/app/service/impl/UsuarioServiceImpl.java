package com.segundito.app.service.impl;

import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.entity.Rol;
import com.segundito.app.entity.Usuario;
import com.segundito.app.exception.ResourceNotFoundException;
import com.segundito.app.repository.RolRepository;
import com.segundito.app.repository.UsuarioRepository;
import com.segundito.app.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario guardar(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setBiografia(usuarioDTO.getBiografia());
        usuario.setFotoPerfil(usuarioDTO.getFotoPerfil());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuario.setActivo(true);

        // Por defecto, asignar rol de usuario normal si no se especifica
        Integer rolId = usuarioDTO.getRolId() != null ? usuarioDTO.getRolId() : 2; // 2 = ROLE_USER
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Integer id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Verificar si el email ya está en uso por otro usuario
        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado por otro usuario");
        }

        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setBiografia(usuarioDTO.getBiografia());

        // Solo actualizar la contraseña si se proporciona una nueva
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        // Si hay cambio de rol y se tiene permisos (verificar en el controller)
        if (usuarioDTO.getRolId() != null) {
            Rol rol = rolRepository.findById(usuarioDTO.getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDTO.getRolId()));
            usuario.setRol(rol);
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // En lugar de eliminar físicamente, se marca como inactivo
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Override
    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public List<Usuario> buscarPorTermino(String termino) {
        return usuarioRepository.buscarPorTermino(termino);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void actualizarUltimoAcceso(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarFotoPerfil(Integer id, String rutaFoto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setFotoPerfil(rutaFoto);
        usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarTopVendedores() {
        return usuarioRepository.findTopVendedores();
    }
}