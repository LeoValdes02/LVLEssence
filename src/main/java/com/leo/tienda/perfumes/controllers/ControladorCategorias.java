package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.servicios.ServicioCategorias;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin/")

public class ControladorCategorias {
    //pido a spring que me de el objeto que tiene de la unica clase que tiene el implements de ServicioVideojuegos
    //dicho objeto, es el objeto de la clase que tiene @Service class ServicioVideojuegosImplJPA implements ServicioVideojuegos
    @Autowired
    private ServicioCategorias servicioCategorias;

    @GetMapping("gestionarCategorias")
    public String gestionarCategorias(Model model) {
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        return "admin/categoria/gestionar_categorias";
    }

    @GetMapping("registrarCategoria")
    public String registrarCategoria(Model model) {
        Categoria nuevo = new Categoria();
        nuevo.setDescripcion("");
        model.addAttribute("nuevo", nuevo);
        // necesito obtener las categorias
        // para meterlas en este model de spring
        // y que le lleguen al html del return
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        return "admin/categoria/registro_categoria";//return devuelve htmls aqui
    }

    @PostMapping("guardarCategoria")
    public String guardarCategoria(@Valid @ModelAttribute("nuevo") Categoria nuevo, BindingResult resultadoValidacion, Model model) {
        System.out.println("recibido para registrar: " + nuevo);
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/categoria/registro_categoria";
        }
        servicioCategorias.registrarCategoria(nuevo);
        return "admin/categoria/registro_categoria_ok";
    }

    @PostMapping("borrarCategoria")
    public String borrarCategoria(@RequestParam("id") Long id, Model model) throws Exception {
        //repaso: model es una variable donde meto lo que quiera que le llegue a la vista indicada en el return
        System.out.println("id recibida para borrar registro: " + id);
        try { servicioCategorias.borrarCategoria(id); }
        catch (IllegalStateException | IllegalArgumentException e) { model.addAttribute("error", e.getMessage()); }
        return gestionarCategorias(model);
    }

  /*  @RequestMapping("ok")
    public String hola(@RequestParam("id") Long id, Model model) {
        System.out.println("id recibido " + id);
        prepararFormularioEditar(id, model);
        System.out.println("perfume a editar: " + servicioPerfumes.obtenerPerfumesPorId(id));
        return "admin/categoria/editar_categoria";
    }*/
    // se puede borrar este de aqui el metodo hola

    @GetMapping("editarCategoria")
    public String editarCategoria(@RequestParam("id") Long id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/categoria/editar_categoria";
    }

//    el metodo va a atender la ruta guardarCambiosPerfume
//    a esa ruta se llega al hacer click en el GUARDAR CAMBIOS en el form de edicion
//    el model atribute es elth:object del form
//    el valid valida las reglas puestas con @ en Pefume.java
//    Perfume perfumeEditar es un objeto metido en el form
//    model en estos metodos es para meterle lo que queramos que le llegue a la vista de vuelta en el result

    @PostMapping("guardaCambiosCategoria")
    public String guardaCambiosCategoria(@Valid @ModelAttribute("categoriaEditar") Categoria categoriaEditar, BindingResult resultadoValidacion, Model model) throws Exception {

        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/categoria/editar_categoria";
        } else {
            servicioCategorias.guardarCambiosCategoria(categoriaEditar);
            return gestionarCategorias(model);
        }
    }

    private void prepararFormularioEditar(Long id, Model model) {
        Categoria categoriaEditar = servicioCategorias.obtenerCategoriaPorId(id);
        model.addAttribute("categoriaEditar", categoriaEditar);
    }


}//end class
