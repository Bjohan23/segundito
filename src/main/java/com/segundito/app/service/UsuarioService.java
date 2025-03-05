package com.segundito.app.service;

import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioService {

    Usuario guardar(UsuarioDTO usuarioDTO);

    Usuario actualizar(Integer id, UsuarioDTO usuarioDTO);

    void eliminar(Integer id);

    Usuario buscarPorId(Integer id);

    Usuario buscarPorEmail(String email);

    Page<Usuario> listarTodos(Pageable pageable);

    List<Usuario> buscarPorTermino(String termino);

    boolean existeEmail(String email);

    void actualizarUltimoAcceso(Integer id);

    void actualizarFotoPerfil(Integer id, String rutaFoto);

    List<Usuario> listarTopVendedores();
}