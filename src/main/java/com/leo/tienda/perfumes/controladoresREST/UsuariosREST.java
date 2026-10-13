package com.leo.tienda.perfumes.controladoresREST;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("restUsuarios/")
public class UsuariosREST {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @RequestMapping("registrarUsuario")
    public String registrarUsuario(@RequestParam Map<String, Object> formData) {

        System.out.println("Datos recibidos del formulario: " + formData);

        // Convertimos los datos recibidos del formulario
        // en un objeto Usuario usando Gson
        Gson gson = new Gson();

        JsonElement json = gson.toJsonTree(formData);

        Usuario usuario = gson.fromJson(json, Usuario.class);

        System.out.println("Usuario generado: " + usuario);

        // Utilizamos el servicio YA existente.
        // El REST no accede directamente a EntityManager.
        servicioUsuarios.registrarUsuario(usuario);

        return "Te has registrado correctamente";
    }
}

/*
Formulario público
       ↓
    FormData
       ↓
POST /restUsuarios/registrarUsuario
       ↓
@RequestParam Map<String,Object>
       ↓
      Gson
       ↓
    Usuario
       ↓
ServicioUsuarios
       ↓
ServicioUsuariosImplJPA
       ↓
    persist()
       ↓
       H2
*/