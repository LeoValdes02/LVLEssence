package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.formularios.FormularioPerfume;
import com.leo.tienda.perfumes.formularios.OpcionesPerfume;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
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
    @Autowired private com.leo.tienda.perfumes.servicios.ServicioGaleria galeria;

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
        FormularioPerfume nuevo = new FormularioPerfume();
        nuevo.setPrecio(1);
        model.addAttribute("nuevo", nuevo);
        prepararOpciones(model, null);
        return "admin/producto/registro_producto";
    }

    @PostMapping("guardarPerfume")
    public String guardarPerfume(@Valid @ModelAttribute("nuevo") FormularioPerfume nuevo,
                                 BindingResult errores, Model model) {
        validarFormulario(nuevo, null, errores);
        if (errores.hasErrors()) {
            prepararOpciones(model, null);
            return "admin/producto/registro_producto";
        }
        nuevo.normalizarNotas();
        try {
            servicioPerfumes.registrarPerfumeConGaleria(nuevo);
            return "admin/producto/registro_producto_ok";
        } catch (IllegalArgumentException error) {
            errores.reject("galeria", error.getMessage());
            prepararOpciones(model, null);
            return "admin/producto/registro_producto";
        }
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
    public String guardaCambiosrPerfume(@Valid @ModelAttribute("perfumeEditar") FormularioPerfume formulario,
                                       BindingResult errores, Model model) throws Exception {
        Perfumes original = servicioPerfumes.obtenerPerfumesPorId(formulario.getId());
        if (original == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        validarFormulario(formulario, original, errores);
        if (errores.hasErrors()) {
            prepararOpciones(model, original);
            return "admin/producto/editar_producto";
        }
        formulario.normalizarNotas();
        try {
            servicioPerfumes.guardarCambiosConGaleria(formulario);
            return "redirect:/admin/gestionarPerfumes";
        } catch (IllegalArgumentException error) {
            errores.reject("galeria", error.getMessage());
            prepararOpciones(model, original);
            return "admin/producto/editar_producto";
        }
    }

    private void prepararFormularioEditar(Long id, Model model) {
        Perfumes original = servicioPerfumes.obtenerPerfumesPorId(id);
        if (original == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        model.addAttribute("perfumeEditar", FormularioPerfume.desde(original));
        prepararOpciones(model, original);
    }

    private <T> List<T> conAnterior(List<T> opciones, T anterior) {
        var valores = new LinkedHashSet<>(opciones);
        if (anterior != null && !anterior.toString().isBlank()) valores.add(anterior);
        return new ArrayList<>(valores);
    }

    private void prepararOpciones(Model model, Perfumes original) {
        var marcas = new LinkedHashSet<>(OpcionesPerfume.MARCAS);
        marcas.addAll(servicioPerfumes.obtenerMarcas());
        model.addAttribute("marcas", conAnterior(new ArrayList<>(marcas), original == null ? null : original.getMarca()));
        model.addAttribute("concentraciones", conAnterior(OpcionesPerfume.CONCENTRACIONES, original == null ? null : original.getConcentracion()));
        model.addAttribute("tamanos", conAnterior(OpcionesPerfume.TAMANOS, original == null || original.getMililitros() <= 0 ? null : original.getMililitros()));
        model.addAttribute("generos", conAnterior(OpcionesPerfume.GENEROS, original == null ? null : original.getGenero()));
        var notas = new LinkedHashSet<>(OpcionesPerfume.NOTAS);
        if (original != null) {
            var anteriores = FormularioPerfume.desde(original);
            notas.addAll(anteriores.getSeleccionSalida());
            notas.addAll(anteriores.getSeleccionCorazon());
            notas.addAll(anteriores.getSeleccionFondo());
        }
        model.addAttribute("notas", new ArrayList<>(notas));
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        model.addAttribute("imagenesGaleria", original == null ? List.of() : galeria.listar(original.getId()));
    }

    private void validarFormulario(FormularioPerfume f, Perfumes original, BindingResult errores) {
        var marcas = new LinkedHashSet<>(OpcionesPerfume.MARCAS);
        marcas.addAll(servicioPerfumes.obtenerMarcas());
        if (f.getMarca() == null || f.getMarca().isBlank() || !marcas.contains(f.getMarca())) errores.rejectValue("marca", "invalida", "Selecciona una marca valida");
        if ((f.getConcentracion() == null || !OpcionesPerfume.CONCENTRACIONES.contains(f.getConcentracion())) && (original == null || f.getConcentracion() == null || !Objects.equals(original.getConcentracion(), f.getConcentracion()))) errores.rejectValue("concentracion", "invalida", "Selecciona una concentracion valida");
        if (!OpcionesPerfume.TAMANOS.contains(f.getMililitros()) && (original == null || f.getMililitros() <= 0 || original.getMililitros() != f.getMililitros())) errores.rejectValue("mililitros", "invalido", "Selecciona un tamano valido");
        if ((f.getGenero() == null || !OpcionesPerfume.GENEROS.contains(f.getGenero())) && (original == null || f.getGenero() == null || !Objects.equals(original.getGenero(), f.getGenero()))) errores.rejectValue("genero", "invalido", "Selecciona un genero valido");
        if (f.getDescripcion() == null || f.getDescripcion().isBlank()) errores.rejectValue("descripcion", "obligatoria", "Introduce una descripcion");
        if (!Double.isFinite(f.getPrecio())) errores.rejectValue("precio", "invalido", "Introduce un precio valido");
        if (f.getStock() == null && original == null) errores.rejectValue("stock", "obligatorio", "Introduce el stock");
        if (servicioCategorias.obtenerCategorias().stream().noneMatch(c -> c.getId() == f.getIdCategoria())) errores.rejectValue("idCategoria", "invalida", "Selecciona una categoria valida");
        FormularioPerfume anterior = original == null ? null : FormularioPerfume.desde(original);
        validarNotas(f.getSeleccionSalida(), anterior == null ? List.of() : anterior.getSeleccionSalida(), "seleccionSalida", errores);
        validarNotas(f.getSeleccionCorazon(), anterior == null ? List.of() : anterior.getSeleccionCorazon(), "seleccionCorazon", errores);
        validarNotas(f.getSeleccionFondo(), anterior == null ? List.of() : anterior.getSeleccionFondo(), "seleccionFondo", errores);
    }
    private void validarNotas(List<String> notas, List<String> anteriores, String campo, BindingResult errores) {
        if (notas != null && notas.stream().anyMatch(n -> n == null || (!OpcionesPerfume.NOTAS.contains(n) && !anteriores.contains(n)))) errores.rejectValue(campo, "invalida", "Selecciona notas validas");
    }
}
