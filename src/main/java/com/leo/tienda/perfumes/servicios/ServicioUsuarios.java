package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Usuario;
import jakarta.validation.Valid;

import java.util.List;

public interface ServicioUsuarios {



    List<Usuario> obtenerUsuarios();

    void registrarUsuario(Usuario usuario);

    void borrarUsuario(int id) throws Exception;

    Usuario obtenerUsuarioPorId(int id);

    Usuario obtenerUsuarioPorEmailYpass(String email, String password);

    void guardarCambiosUsuario(@Valid Usuario usuarioEditar) throws Exception;
}
