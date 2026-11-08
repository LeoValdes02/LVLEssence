package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.constantesSQL.ConstantesSQL;
import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Locale;

@Service //marcas que la clase actua como servicio de datos
@Transactional // los metodos de la clase se ejecutan como una unica transaccion
// Transaccion:
public class ServicioPerfumesImplJPA implements ServicioPerfumes {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrarPerfume(Perfumes p) {
        asignarCategoriaDesdeFormulario(p);
        //el perfume ahora mismo tiene la fotoSubida, pero
        // en imagenPortada no tiene nada.
        try {
            p.setImagenPortada(p.getFotoSubida().getBytes());
        } catch (IOException e) {
            System.out.println("NO PUDE PROCESAR LA FOTO SUBIDA");
            throw new RuntimeException(e);
        }
        entityManager.persist(p);
    }

    @Override
    public List obtenerPerfumes() {
        // lanzar JPQL
        // la consulta lanzada equivale al sql: select * from tabala_perfumes as p
        return entityManager.createQuery("select p from Perfumes p").getResultList();
    }

    private static final int RESULTADOS_POR_PAGINA = 10;

    // Mismo precio final que en Java (redondeado a céntimos), pero calculado
    // en H2 para poder ordenar/filtrar ANTES de paginar.
    // COALESCE trata los perfumes anteriores sin descuento como 0 %.
    private static final String PRECIO_CATALOGO =
            "round(p.precio * (100 - coalesce(p.descuentoPorcentaje, 0)) / 100.0, 2)";

    @Override
    public PaginaPerfumes buscarPerfumes(String texto, Integer categoriaId,
                                         Double precioMin, Double precioMax,
                                         String orden, int pagina) {

        if (pagina < 1 || pagina > Integer.MAX_VALUE / RESULTADOS_POR_PAGINA) {
            throw new IllegalArgumentException("La página debe ser un número positivo válido");
        }
        if (categoriaId != null && categoriaId < 1) {
            throw new IllegalArgumentException("La categoría no es válida");
        }
        if (precioMin != null && (!Double.isFinite(precioMin) || precioMin < 0)) {
            throw new IllegalArgumentException("El precio mínimo debe ser positivo o cero");
        }
        if (precioMax != null && (!Double.isFinite(precioMax) || precioMax < 0)) {
            throw new IllegalArgumentException("El precio máximo debe ser positivo o cero");
        }
        if (precioMin != null && precioMax != null && precioMin > precioMax) {
            throw new IllegalArgumentException("El precio mínimo no puede superar al máximo");
        }

        // Ordenación por valores permitidos, nunca se concatena SQL procedente del usuario.
        String ordenJpql = switch (orden == null ? "nombreAsc" : orden) {
            case "nombreAsc" -> "lower(p.nombre) asc, p.id asc";
            case "nombreDesc" -> "lower(p.nombre) desc, p.id asc";
            case "precioAsc" -> PRECIO_CATALOGO + " asc, p.id asc";
            case "precioDesc" -> PRECIO_CATALOGO + " desc, p.id asc";
            case "recientes" -> "p.id desc";
            default -> throw new IllegalArgumentException("Ordenación no válida");
        };

        String textoLimpio = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        // ! evita que los caracteres % y _ del usuario actúen como comodines SQL.
        String patron = "%" + textoLimpio.replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_") + "%";

        StringBuilder filtros = new StringBuilder(" where lower(p.nombre) like :texto escape '!' ");
        if (categoriaId != null) filtros.append(" and p.categoria.id = :categoriaId ");
        if (precioMin != null) filtros.append(" and ").append(PRECIO_CATALOGO).append(" >= :precioMin ");
        if (precioMax != null) filtros.append(" and ").append(PRECIO_CATALOGO).append(" <= :precioMax ");

        TypedQuery<Long> consultaTotal = entityManager.createQuery(
                "select count(p.id) from Perfumes p" + filtros, Long.class);
        aplicarFiltros(consultaTotal, patron, categoriaId, precioMin, precioMax);
        long total = consultaTotal.getSingleResult();

        // Proyectar solo campos usados en las tarjetas; nunca cargar la imagen BLOB en el JSON.
        TypedQuery<PerfumeResumen> consulta = entityManager.createQuery(
                "select new com.leo.tienda.perfumes.responseData.PerfumeResumen(" +
                "p.id, p.nombre, p.marca, " + PRECIO_CATALOGO +
                ", p.precio, coalesce(p.descuentoPorcentaje, 0), c.nombre) " +
                "from Perfumes p left join p.categoria c" + filtros +
                " order by " + ordenJpql, PerfumeResumen.class);
        aplicarFiltros(consulta, patron, categoriaId, precioMin, precioMax);
        consulta.setFirstResult((pagina - 1) * RESULTADOS_POR_PAGINA);
        consulta.setMaxResults(RESULTADOS_POR_PAGINA);

        long totalPaginas = (total + RESULTADOS_POR_PAGINA - 1) / RESULTADOS_POR_PAGINA;
        return new PaginaPerfumes(consulta.getResultList(), total, pagina,
                RESULTADOS_POR_PAGINA, totalPaginas);
    }

    private void aplicarFiltros(TypedQuery<?> consulta, String patron,
                               Integer categoriaId, Double precioMin, Double precioMax) {
        consulta.setParameter("texto", patron);
        if (categoriaId != null) consulta.setParameter("categoriaId", categoriaId);
        if (precioMin != null) consulta.setParameter("precioMin", precioMin);
        if (precioMax != null) consulta.setParameter("precioMax", precioMax);
    }

    @Override
    public List<Perfumes> obtenerPerfumesPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        // Una consulta para recuperar los campos completos de las tarjetas de administración.
        List<Perfumes> encontrados = entityManager.createQuery(
                "select p from Perfumes p where p.id in :ids", Perfumes.class)
                .setParameter("ids", ids)
                .getResultList();

        // IN no garantiza el orden de la página; restaurar el orden de búsqueda.
        Map<Long, Perfumes> porId = new HashMap<>();
        for (Perfumes perfume : encontrados) porId.put(perfume.getId(), perfume);

        List<Perfumes> ordenados = new ArrayList<>();
        for (Long id : ids) {
            Perfumes perfume = porId.get(id);
            if (perfume != null) ordenados.add(perfume);
        }
        return ordenados;
    }

    @Override
    public void aplicarDescuentoEnLote(List<Long> ids, int porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100 %");
        }
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos un perfume");
        }
        List<Long> unicos = ids.stream().distinct().toList();
        if (unicos.size() > 100) {
            throw new IllegalArgumentException("Selecciona como máximo 100 perfumes por operación");
        }
        List<Perfumes> perfumes = entityManager.createQuery(
                "SELECT p FROM Perfumes p WHERE p.id IN :ids", Perfumes.class)
                .setParameter("ids", unicos)
                .getResultList();
        if (perfumes.size() != unicos.size()) {
            throw new IllegalArgumentException("Alguno de los perfumes seleccionados ya no existe");
        }
        for (Perfumes perfume : perfumes) {
            perfume.setDescuentoPorcentaje(porcentaje);
        }
    }

    @Override
    public Perfumes obtenerPerfumesPorId(long id) {
        return entityManager.find(Perfumes.class, id);
    }

    @Override
    public void borrarPerfume(long id) throws Exception {

        //En JPA la forma mas comun de borrar un registro
        // es pedirlo, comprobar que existe y luego borrarlo

        Perfumes perfumeAborrar = entityManager.find(Perfumes.class, id);
        if (perfumeAborrar != null) {
            Long pedidos = entityManager.createQuery("select count(p) from ProductoPedido p where p.perfume.id = :id", Long.class).setParameter("id", id).getSingleResult();
            Long carritos = entityManager.createQuery("select count(p) from ProductoCarrito p where p.perfume.id = :id", Long.class).setParameter("id", id).getSingleResult();
            if (pedidos > 0 || carritos > 0) throw new IllegalStateException("No se puede borrar un perfume incluido en pedidos o carritos.");
            entityManager.remove(perfumeAborrar);

        } else {
            throw new IllegalArgumentException("Perfume no encontrado");
        }

    }

    @Override
    public void guardarCambiosPerfume(Perfumes perfumeEditar) throws Exception {
        asignarCategoriaDesdeFormulario(perfumeEditar);

//      para no perder la foto del perfume, debemos ver si el usuario
//      le ha asignado una nueva foto en el caso contrario le cargamos la anterior
        if (perfumeEditar.getFotoSubida().getSize() == 0) {
            Perfumes original =
                    entityManager.find(Perfumes.class, perfumeEditar.getId());
            perfumeEditar.setImagenPortada(original.getImagenPortada());
        } else {
            perfumeEditar.setImagenPortada(perfumeEditar.getFotoSubida().getBytes());
        }


//      merge actualiza el registro ai ya existe
//       si no existe
        entityManager.merge(perfumeEditar);
    }

    @Override
    public List<Map<String, Object>> obtenerJSONperfumes() {
        return ((NativeQueryImpl) entityManager.createNativeQuery(ConstantesSQL.SQL_OBTENER_PERFUMES_JSON))
                .setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList();
    }

    @Override
    public Map<String, Object> obtenerJSONPerfumesPorId(Long id) {
        return (Map<String, Object>) ((NativeQueryImpl) entityManager.createNativeQuery(ConstantesSQL.SQL_OBTENER_PERFUME_JSON))
                .setParameter("id", id)
                .setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList()
                .get(0);
    }

    private void asignarCategoriaDesdeFormulario(Perfumes perfume) {
        if (perfume.getIdCategoria() > 0) {
            perfume.setCategoria(entityManager.find(Categoria.class, perfume.getIdCategoria()));
        }
    }
}
