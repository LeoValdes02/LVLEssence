package com.leo.tienda.perfumes.controladoresREST;

import com.google.gson.Gson;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("restUsuarios/")
public class UsuariosREST {

    @Autowired
    private ServicioUsuarios servicioUsuarios;


    @PostMapping("registrarUsuario")
    public String registrarUsuario(@RequestParam Map<String, Object> formData) {

        Gson gson = new Gson();

        Usuario usuario = gson.fromJson(gson.toJsonTree(formData), Usuario.class);

        try {
            servicioUsuarios.registrarUsuario(usuario);
            return "Te has registrado correctamente";

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    @PostMapping("identificarUsuario")
    public String identificarUsuario(@RequestParam("email") String email, @RequestParam("pass") String password, HttpServletRequest request) {
        Usuario usuario = servicioUsuarios.obtenerUsuarioPorEmailYpass(email, password);
        if (usuario != null) {
            request.getSession(true);
            request.changeSessionId();
            request.getSession().setAttribute("usuario_identificado", usuario);
            return "Te has identificado correctamente";
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    @GetMapping("sesion")
    public Map<String, Object> sesion(HttpServletRequest request) {
        var sesion = request.getSession(false);
        Usuario usuario = sesion == null ? null : (Usuario) sesion.getAttribute("usuario_identificado");
        if (usuario == null) return Map.of("identificado", false);
        return Map.of("identificado", true, "nombre", usuario.getNombre());
    }

    @PostMapping("cerrarSesion")
    public void cerrarSesion(HttpServletRequest request) {
        var sesion = request.getSession(false);
        // Conserva una posible sesión administrativa compartida en el mismo navegador.
        if (sesion != null) sesion.removeAttribute("usuario_identificado");
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
