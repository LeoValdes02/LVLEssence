package com.leo.tienda.perfumes.controladoresREST;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioDescubrimiento;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/restValoraciones")
public class ValoracionesREST {
    private final ServicioDescubrimiento servicio;
    public ValoracionesREST(ServicioDescubrimiento s) { servicio=s; }
    private Usuario usuario(HttpServletRequest req) { var sesion=req.getSession(false); return sesion==null ? null : (Usuario)sesion.getAttribute("usuario_identificado"); }
    @GetMapping public Map<String,Object> resumen(@RequestParam long id, HttpServletRequest req) {
        try { Usuario u=usuario(req); return servicio.resumen(id, u==null ? null : u.getId()); }
        catch(NoSuchElementException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()); }
    }
    @PostMapping public Map<String,Object> votar(@RequestParam long id, @RequestParam int puntuacion, HttpServletRequest req) {
        Usuario u=usuario(req); if(u==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Debes identificarte");
        try { servicio.valorar(u.getId(),id,puntuacion); return servicio.resumen(id,u.getId()); }
        catch(IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage()); }
        catch(NoSuchElementException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage()); }
    }
}
