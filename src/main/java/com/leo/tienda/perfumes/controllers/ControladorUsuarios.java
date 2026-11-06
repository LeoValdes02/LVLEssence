package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin/")
public class ControladorUsuarios {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @GetMapping("gestionarUsuarios")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
        return "admin/usuario/gestionar_usuarios";
    }

    @GetMapping("registrarUsuario")
    public String registrarUsuario(Model model) {
        model.addAttribute("nuevo", new Usuario());
        model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
        return "admin/usuario/registro_usuario";
    }

    @PostMapping("guardarUsuario")
    public String guardarUsuario(@Valid @ModelAttribute("nuevo") Usuario nuevo, BindingResult resultadoValidacion, Model model) {

        if (resultadoValidacion.hasErrors()) {
            return "admin/usuario/registro_usuario";
        }

        try {
            servicioUsuarios.registrarUsuario(nuevo);
            return "admin/usuario/registro_usuario_ok";

        } catch (IllegalArgumentException e) {

            resultadoValidacion.rejectValue("email", "email.duplicado", e.getMessage());

            return "admin/usuario/registro_usuario";
        }
    }


    @PostMapping("borrarUsuario")
    public String borrarUsuario(@RequestParam("id") int id, Model model) throws Exception {

        try {
            servicioUsuarios.borrarUsuario(id);

            model.addAttribute("mensaje", "Usuario eliminado correctamente.");

        } catch (IllegalStateException | IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
        }

        return gestionarUsuarios(model);
    }


    @GetMapping("editarUsuario")
    public String editarUsuario(@RequestParam("id") int id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/usuario/editar_usuario";
    }

    @PostMapping("guardaCambiosUsuario")
    public String guardaCambiosUsuario(@Valid @ModelAttribute("usuarioEditar") Usuario usuarioEditar, BindingResult resultadoValidacion, Model model) throws Exception {

        if (resultadoValidacion.hasErrors()) {
            return "admin/usuario/editar_usuario";
        }

        try {
            servicioUsuarios.guardarCambiosUsuario(usuarioEditar);
            return gestionarUsuarios(model);

        } catch (IllegalArgumentException e) {

            resultadoValidacion.rejectValue("email", "email.duplicado", e.getMessage());

            return "admin/usuario/editar_usuario";
        }
    }

    private void prepararFormularioEditar(int id, Model model) {
        Usuario usuarioEditar = servicioUsuarios.obtenerUsuarioPorId(id);
        model.addAttribute("usuarioEditar", usuarioEditar);
    }
}
