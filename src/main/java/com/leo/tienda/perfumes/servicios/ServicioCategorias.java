package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Categoria;
import jakarta.validation.Valid;

import java.util.List;

public interface ServicioCategorias {

    List<Categoria> obtenerCategorias();//LISTAR

    void registrarCategoria(Categoria c); //

    void borrarCategoria(long id) throws Exception; //

    Categoria obtenerCategoriaPorId(long id); //

    void guardarCambiosCategoria(@Valid Categoria categoriaEditar) throws Exception; //EDITAR

}
