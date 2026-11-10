package com.leo.tienda.perfumes.servicios;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import java.util.List;
public interface ServicioFavoritos {
    List<PerfumeResumen> listar(int usuario);
    void agregar(int usuario, long perfume);
    void eliminar(int usuario, long perfume);
}
