package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.model.ImagenPerfume;
import com.leo.tienda.perfumes.servicios.ServicioGaleria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/restPerfumes")
public class GaleriaREST {
    private final ServicioGaleria galeria;
    public GaleriaREST(ServicioGaleria galeria) { this.galeria=galeria; }
    @GetMapping("/galerias")
    public List<ServicioGaleria.Galeria> galerias(@RequestParam List<Long> ids) {
        try { return galeria.galerias(ids); }
        catch (IllegalArgumentException error) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,error.getMessage()); }
    }
    @GetMapping("/imagenes/{id}")
    public ResponseEntity<byte[]> imagen(@PathVariable long id) {
        ImagenPerfume imagen=galeria.imagen(id);
        if(imagen==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(imagen.getTipo()))
                .header("X-Content-Type-Options","nosniff")
                .body(imagen.getContenido());
    }
}
