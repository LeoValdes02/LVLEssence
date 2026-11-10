package com.leo.tienda.perfumes.servicios;
import java.util.List;
import java.util.Map;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
public interface ServicioDescubrimiento {
    void valorar(int usuario, long perfume, int puntuacion);
    Map<String,Object> resumen(long perfume, Integer usuario);
    List<Map<String,Object>> mejorValorados();
    List<PerfumeResumen> recomendar(Long referencia);
}
