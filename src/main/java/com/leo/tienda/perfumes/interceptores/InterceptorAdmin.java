package com.leo.tienda.perfumes.interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorAdmin implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String ruta = request.getRequestURI();
        String prefijoAdmin = request.getContextPath() + "/admin/";

        // Solo proteger las rutas de administración
        if (ruta.equals(request.getContextPath() + "/admin") || ruta.startsWith(prefijoAdmin)) {

            HttpSession sesion = request.getSession(false);

            // Comprobar si existe una sesión administrativa
            if (sesion == null ||
                    !"ok".equals(sesion.getAttribute("admin"))) {

                response.sendRedirect(
                        request.getContextPath() + "/loginAdmin"
                );

                return false;
            }

            // Evitar almacenar páginas administrativas en caché
            response.setHeader("Cache-Control", "no-store");
        }

        return true;
    }
}
