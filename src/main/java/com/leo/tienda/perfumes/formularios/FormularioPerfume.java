package com.leo.tienda.perfumes.formularios;

import com.leo.tienda.perfumes.model.Perfumes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormularioPerfume extends Perfumes {
    private List<org.springframework.web.multipart.MultipartFile> fotosAdicionales = new ArrayList<>();
    private List<Long> eliminarImagenes = new ArrayList<>();
    private java.util.Map<Long, Integer> ordenImagenes = new java.util.LinkedHashMap<>();
    public List<org.springframework.web.multipart.MultipartFile> getFotosAdicionales() { return fotosAdicionales; }
    public void setFotosAdicionales(List<org.springframework.web.multipart.MultipartFile> fotos) { fotosAdicionales = fotos; }
    public List<Long> getEliminarImagenes() { return eliminarImagenes; }
    public void setEliminarImagenes(List<Long> ids) { eliminarImagenes = ids; }
    public java.util.Map<Long, Integer> getOrdenImagenes() { return ordenImagenes; }
    public void setOrdenImagenes(java.util.Map<Long, Integer> orden) { ordenImagenes = orden; }
    private List<String> seleccionSalida = new ArrayList<>();
    private List<String> seleccionCorazon = new ArrayList<>();
    private List<String> seleccionFondo = new ArrayList<>();
    public List<String> getSeleccionSalida() { return seleccionSalida; }
    public void setSeleccionSalida(List<String> v) { seleccionSalida = normalizarSeleccion(v); }
    public List<String> getSeleccionCorazon() { return seleccionCorazon; }
    public void setSeleccionCorazon(List<String> v) { seleccionCorazon = normalizarSeleccion(v); }
    public List<String> getSeleccionFondo() { return seleccionFondo; }
    public void setSeleccionFondo(List<String> v) { seleccionFondo = normalizarSeleccion(v); }
    private static List<String> normalizarSeleccion(List<String> valores) {
        return valores == null ? new ArrayList<>() : valores.stream().map(OpcionesPerfume::normalizarNota).distinct().toList();
    }
    private static List<String> separar(String s) {
        return s == null || s.isBlank() ? new ArrayList<>() : new ArrayList<>(Arrays.stream(s.split(",")).map(OpcionesPerfume::normalizarNota).filter(x -> !x.isEmpty()).distinct().toList());
    }
    public static FormularioPerfume desde(Perfumes p) {
        FormularioPerfume f = new FormularioPerfume();
        f.setId(p.getId()); f.setNombre(p.getNombre()); f.setMarca(p.getMarca());
        f.setPrecio(p.getPrecio()); f.setDescuentoPorcentaje(p.getDescuentoPorcentaje());
        f.setFamiliaOlfativa(p.getFamiliaOlfativa()); f.setMililitros(p.getMililitros());
        f.setConcentracion(p.getConcentracion()); f.setGenero(p.getGenero()); f.setDescripcion(p.getDescripcion());
        f.setStock(p.getStock()); f.setIdCategoria(p.getCategoria() == null ? 0 : p.getCategoria().getId());
        f.setSeleccionSalida(separar(p.getNotasSalida())); f.setSeleccionCorazon(separar(p.getNotasCorazon())); f.setSeleccionFondo(separar(p.getNotasFondo()));
        return f;
    }
    public Perfumes paraCrear() {
        Perfumes p = new Perfumes(getNombre(), getMarca(), getPrecio(), getFamiliaOlfativa(), getMililitros(), getConcentracion(), getGenero(), getDescripcion());
        p.setIdCategoria(getIdCategoria()); p.setDescuentoPorcentaje(getDescuentoPorcentaje()); p.setStock(getStock());
        p.setNotasSalida(getNotasSalida()); p.setNotasCorazon(getNotasCorazon()); p.setNotasFondo(getNotasFondo()); p.setFotoSubida(getFotoSubida());
        return p;
    }
    public void normalizarNotas() {
        setNotasSalida(unir(seleccionSalida)); setNotasCorazon(unir(seleccionCorazon)); setNotasFondo(unir(seleccionFondo));
    }
    private static String unir(List<String> valores) {
        return valores == null || valores.isEmpty() ? null : String.join(", ", valores.stream().distinct().toList());
    }
}
