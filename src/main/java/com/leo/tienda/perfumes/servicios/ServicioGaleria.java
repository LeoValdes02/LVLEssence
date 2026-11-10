package com.leo.tienda.perfumes.servicios;
import com.leo.tienda.perfumes.model.ImagenPerfume;
import com.leo.tienda.perfumes.model.Perfumes;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ServicioGaleria {
    record Meta(long id, int orden) {}
    record Galeria(long id, List<String> imagenes) {}
    void actualizar(Perfumes perfume, MultipartFile portada, List<MultipartFile> nuevas, List<Long> eliminar, Map<Long,Integer> orden);
    List<Meta> listar(long idPerfume);
    List<Galeria> galerias(List<Long> ids);
    ImagenPerfume imagen(long id);
}
