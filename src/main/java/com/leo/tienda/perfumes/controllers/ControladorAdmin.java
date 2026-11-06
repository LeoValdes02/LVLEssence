package com.leo.tienda.perfumes.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorAdmin {

    // Contraseña definida fuera del código Java
    private final String passwordAdmin = "123";

    // Mostrar formulario de identificación
    @GetMapping("/loginAdmin")
    public String loginAdmin() {
        return "admin/login_admin";
    }

    // Procesar identificación mediante POST
    @PostMapping("/loginAdmin")
    public String procesarLogin(
            @RequestParam("pass-login-admin") String password,
            HttpServletRequest request) {

        if (!passwordAdmin.isBlank() &&
                passwordAdmin.equals(password)) {

            HttpSession sesion = request.getSession(true);

            // Renovar el identificador de sesión
            request.changeSessionId();

            sesion.setAttribute("admin", "ok");

            return "redirect:/admin/gestionarPerfumes";
        }

        return "redirect:/loginAdmin?error=1";
    }

    // Página de inicio del administrador
    @GetMapping("/admin/inicioAdmin")
    public String inicioAdmin() {
        return "admin/inicio_admin";
    }

    // Cerrar únicamente la sesión de administrador
    @PostMapping("/admin/cerrarSesion")
    public String cerrarSesion(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.removeAttribute("admin");
        }

        return "redirect:/loginAdmin";
    }
}
