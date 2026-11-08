package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import com.leo.tienda.perfumes.servicios.ServicioCategorias;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.LongStream;

@Controller
@RequestMapping("admin/")
public class ControladorPerfumes {

    @Autowired
    private ServicioPerfumes servicioPerfumes;

    @Autowired
    private ServicioCategorias servicioCategorias;

    /** Misma búsqueda que el catálogo público, pero renderizada con Thymeleaf. */
    @GetMapping("gestionarPerfumes")
    public String gestionarPerfumes(
            @RequestParam(defaultValue = "") String texto,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        model.addAttribute("texto", texto);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("orden", orden);

        try {
            PaginaPerfumes resultado = servicioPerfumes.buscarPerfumes(
                    texto, categoriaId, precioMin, precioMax, orden, pagina);

            // Si se borra el último elemento de una página, mostrar la última disponible.
            if (pagina > 1 && pagina > Math.max(1L, resultado.totalPaginas())) {
                pagina = (int) Math.max(1L, resultado.totalPaginas());
                resultado = servicioPerfumes.buscarPerfumes(
                        texto, categoriaId, precioMin, precioMax, orden, pagina);
            }

            // La proyección pública solo tiene nombre, marca, precio y categoría.
            // Para administración, recuperar los 10 objetos completos en una sola consulta.
            List<Long> ids = resultado.perfumes().stream().map(PerfumeResumen::id).toList();
            model.addAttribute("perfumes", servicioPerfumes.obtenerPerfumesPorIds(ids));
            model.addAttribute("total", resultado.total());
            model.addAttribute("paginaActual", pagina);
            model.addAttribute("totalPaginas", resultado.totalPaginas());
            model.addAttribute("desde", resultado.total() == 0 ? 0 : (pagina - 1L) * resultado.tamano() + 1);
            model.addAttribute("hasta", Math.min(resultado.total(), (long) pagina * resultado.tamano()));

            long inicio = Math.max(1L, pagina - 2L);
            long fin = Math.min(resultado.totalPaginas(), inicio + 4L);
            inicio = Math.max(1L, fin - 4L);
            model.addAttribute("numerosPagina", fin < inicio
                    ? List.of() : LongStream.rangeClosed(inicio, fin).boxed().toList());
        } catch (IllegalArgumentException error) {
            model.addAttribute("error", error.getMessage());
            model.addAttribute("perfumes", List.of());
            model.addAttribute("total", 0L);
            model.addAttribute("paginaActual", 1);
            model.addAttribute("totalPaginas", 0L);
            model.addAttribute("desde", 0);
            model.addAttribute("hasta", 0L);
            model.addAttribute("numerosPagina", List.of());
        }

        return "admin/producto/gestionar_productos";
    }

    @GetMapping("registrarPerfume")
    public String registrarPerfume(Model model) {
        Perfumes nuevo = new Perfumes();
        nuevo.setPrecio(1);
        model.addAttribute("nuevo", nuevo);
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        return "admin/producto/registro_producto";
    }

    @PostMapping("guardarPerfume")
    public String guardarPerfume(@Valid @ModelAttribute("nuevo") Perfumes nuevo,
                                 BindingResult resultadoValidacion, Model model) {
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/producto/registro_producto";
        }
        servicioPerfumes.registrarPerfume(nuevo);
        return "admin/producto/registro_producto_ok";
    }

    /** Tras borrar, redirigir (PRG) y conservar filtros. No permite borrado por GET. */
    @PostMapping("borrarPerfume")
    public String borrarPerfume(
            @RequestParam("id") Long id,
            @RequestParam(defaultValue = "") String texto,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            RedirectAttributes redirect) throws Exception {
        try {
            servicioPerfumes.borrarPerfume(id);
            redirect.addFlashAttribute("mensaje", "Perfume eliminado correctamente.");
        } catch (IllegalStateException | IllegalArgumentException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }

        redirect.addAttribute("texto", texto);
        if (categoriaId != null) redirect.addAttribute("categoriaId", categoriaId);
        if (precioMin != null) redirect.addAttribute("precioMin", precioMin);
        if (precioMax != null) redirect.addAttribute("precioMax", precioMax);
        redirect.addAttribute("orden", orden);
        redirect.addAttribute("pagina", pagina);
        return "redirect:/admin/gestionarPerfumes";
    }

    /** Descuento por lotes: solo POST y únicamente para los IDs seleccionados. */
    @PostMapping("aplicarDescuento")
    public String aplicarDescuento(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam("porcentajeDescuento") int porcentaje,
            @RequestParam(defaultValue = "") String texto,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina,
            RedirectAttributes redirect) {
        try {
            servicioPerfumes.aplicarDescuentoEnLote(ids, porcentaje);
            redirect.addFlashAttribute("mensaje", "Oferta actualizada en los perfumes seleccionados.");
        } catch (IllegalArgumentException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        redirect.addAttribute("texto", texto);
        if (categoriaId != null) redirect.addAttribute("categoriaId", categoriaId);
        if (precioMin != null) redirect.addAttribute("precioMin", precioMin);
        if (precioMax != null) redirect.addAttribute("precioMax", precioMax);
        redirect.addAttribute("orden", orden);
        redirect.addAttribute("pagina", pagina);
        return "redirect:/admin/gestionarPerfumes";
    }

    @GetMapping("ok")
    public String hola(@RequestParam("id") Long id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/producto/editar_producto";
    }

    @GetMapping("editarPerfume")
    public String editarPerfume(@RequestParam("id") Long id, Model model) {
        prepararFormularioEditar(id, model);
        return "admin/producto/editar_producto";
    }

    @PostMapping("guardaCambiosrPerfume")
    public String guardaCambiosrPerfume(@Valid @ModelAttribute("perfumeEditar") Perfumes perfumeEditar,
                                       BindingResult resultadoValidacion, Model model) throws Exception {
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/producto/editar_producto";
        }
        servicioPerfumes.guardarCambiosPerfume(perfumeEditar);
        return "redirect:/admin/gestionarPerfumes";
    }

    private void prepararFormularioEditar(Long id, Model model) {
        Perfumes perfumeEditar = servicioPerfumes.obtenerPerfumesPorId(id);
        if (perfumeEditar.getCategoria() != null) {
            perfumeEditar.setIdCategoria(perfumeEditar.getCategoria().getId());
        }
        model.addAttribute("perfumeEditar", perfumeEditar);
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
    }
}
