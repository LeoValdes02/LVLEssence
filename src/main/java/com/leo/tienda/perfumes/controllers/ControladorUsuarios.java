package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin/")
public class ControladorUsuarios {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @RequestMapping("gestionarUsuarios")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
        return "admin/usuario/gestionar_usuarios";
    }

    @RequestMapping("registrarUsuario")
    public String registrarUsuario(Model model) {
        model.addAttribute("nuevo", new Usuario());
        model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
        return "admin/usuario/registro_usuario";
    }

    @RequestMapping("guardarUsuario")
    public String guardarUsuario(@Valid @ModelAttribute("nuevo") Usuario nuevo, BindingResult resultadoValidacion, Model model) {
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
            return "admin/usuario/registro_usuario";
        }
        servicioUsuarios.registrarUsuario(nuevo);
        return "admin/usuario/registro_usuario_ok";
    }

    @RequestMapping("borrarUsuario")
    public String borrarUsuario(@RequestParam("id") int id, Model model) throws Exception {
        servicioUsuarios.borrarUsuario(id);
        return gestionarUsuarios(model);
    }

    @RequestMapping("editarUsuario")
    public String editarUsuario(@RequestParam("id") int id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/usuario/editar_usuario";
    }

    @RequestMapping("guardaCambiosUsuario")
    public String guardaCambiosUsuario(@Valid @ModelAttribute("usuarioEditar") Usuario usuarioEditar, BindingResult resultadoValidacion, Model model) throws Exception {
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
            return "admin/usuario/editar_usuario";
        }
        servicioUsuarios.guardarCambiosUsuario(usuarioEditar);
        return gestionarUsuarios(model);
    }

    private void prepararFormularioEditar(int id, Model model) {
        Usuario usuarioEditar = servicioUsuarios.obtenerUsuarioPorId(id);
        model.addAttribute("usuarioEditar", usuarioEditar);
    }
}
