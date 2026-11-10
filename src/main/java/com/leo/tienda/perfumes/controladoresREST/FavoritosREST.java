package com.leo.tienda.perfumes.controladoresREST;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import com.leo.tienda.perfumes.servicios.ServicioFavoritos;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/restFavoritos")
public class FavoritosREST {
    private final ServicioFavoritos favoritos;
    public FavoritosREST(ServicioFavoritos favoritos) { this.favoritos = favoritos; }
    private int usuario(HttpServletRequest request) {
        var sesion = request.getSession(false);
        Usuario u = sesion == null ? null : (Usuario) sesion.getAttribute("usuario_identificado");
        if (u == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes identificarte");
        return u.getId();
    }
    @GetMapping public List<PerfumeResumen> listar(HttpServletRequest request) { return favoritos.listar(usuario(request)); }
    @PostMapping public void agregar(@RequestParam long id, HttpServletRequest request) {
        try { favoritos.agregar(usuario(request), id); } catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); }
    }
    @DeleteMapping public void eliminar(@RequestParam long id, HttpServletRequest request) { favoritos.eliminar(usuario(request), id); }
}
