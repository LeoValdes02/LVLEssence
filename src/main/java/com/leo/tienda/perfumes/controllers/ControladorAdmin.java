package com.leo.tienda.perfumes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ControladorAdmin {

    // la redireccion que hace el interceptor
    // sera atendida aqui, que es el unico sitio
    // donde se puede devolver una vista
    @RequestMapping("loginAdmin")
    public String loginAdmin(){
        return "admin/login_admin";
    }

    @RequestMapping("admin/inicioAdmin")
    public String inicioAdmin(){
        return "admin/inicio_admin";
    }

}
