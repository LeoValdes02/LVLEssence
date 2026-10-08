package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Perfume;
import com.leo.tienda.perfumes.servicios.ServicioCategorias;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
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
public class ControladorPerfumes {

    //pido a spring que me de el objeto que tiene de la unica clase que tiene el implements de ServicioVideojuegos
    //dicho objeto, es el objeto de la clase que tiene @Service class ServicioVideojuegosImplJPA implements ServicioVideojuegos
    @Autowired
    private ServicioPerfumes servicioPerfumes;
    @Autowired
    private ServicioCategorias servicioCategorias;

    @RequestMapping("gestionarPerfumes")
    public String gestionarPerfumes(Model model) {
        model.addAttribute("perfumes", servicioPerfumes.obtenerPerfumes());
        return "admin/gestionar_productos";
    }

    @RequestMapping("registrarPerfume")
    public String registrarPerfume(Model model) {
        Perfume nuevo = new Perfume();
        nuevo.setPrecio(1);
        model.addAttribute("nuevo", nuevo);
        // necesito obtener las categorias
        // para meterlas en este model de spring
        // y que le lleguen al html del return
        model.addAttribute("categorias", servicioCategorias.ObtenerCategorias());
        return "admin/registro_producto";//return devuelve htmls aqui
    }

    @RequestMapping("guardarPerfume")
    public String guardarPerfume(@Valid @ModelAttribute("nuevo") Perfume nuevo, BindingResult resultadoValidacion, Model model) {
        System.out.println("recibido para registrar: " + nuevo);
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.ObtenerCategorias());
            return "admin/registro_producto";
        }
        servicioPerfumes.registrarPerfume(nuevo);
        return "admin/registro_producto_ok";
    }

    @RequestMapping("borrarPerfume")
    public String borrarPerfume(@RequestParam("id") Long id, Model model) throws Exception {
        //repaso: model es una variable donde meto lo que quiera que le llegue a la vista indicada en el return
        System.out.println("id recibida para borrar registro: " + id);
        servicioPerfumes.borrarPerfume(id);
        return gestionarPerfumes(model);
    }

    @RequestMapping("ok")
    public String hola(@RequestParam("id") Long id, Model model) {
        System.out.println("id recibido " + id);
        prepararFormularioEditar(id, model);
        System.out.println("perfume a editar: " + servicioPerfumes.obtenerPerfumesPorId(id));
        return "admin/editar_producto";
    }

    @RequestMapping("editarPerfume")
    public String editarPerfume(@RequestParam("id") Long id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/editar_producto";
    }

//    el metodo va a atender la ruta guardarCambiosPerfume
//    a esa ruta se llega al hacer click en el GUARDAR CAMBIOS en el form de edicion
//    el model atribute es elth:object del form
//    el valid valida las reglas puestas con @ en Pefume.java
//    Perfume perfumeEditar es un objeto metido en el form
//    model en estos metodos es para meterle lo que queramos que le llegue a la vista de vuelta en el result

    @RequestMapping("guardaCambiosrPerfume")
    public String guardaCambiosrPerfume(@Valid @ModelAttribute("perfumeEditar") Perfume perfumeEditar, BindingResult resultadoValidacion, Model model) throws Exception {

        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.ObtenerCategorias());
            return "admin/editar_producto";
        } else {
            servicioPerfumes.guardarCambiosPerfume(perfumeEditar);
            return gestionarPerfumes(model);
        }
    }

    private void prepararFormularioEditar(Long id, Model model) {
        Perfume perfumeEditar = servicioPerfumes.obtenerPerfumesPorId(id);
        if (perfumeEditar.getCategoria() != null) {
            perfumeEditar.setIdCategoria((int) perfumeEditar.getCategoria().getId());
        }
        model.addAttribute("perfumeEditar", perfumeEditar);
        model.addAttribute("categorias", servicioCategorias.ObtenerCategorias());
    }


}//end class guardaCambiosrPerfume
