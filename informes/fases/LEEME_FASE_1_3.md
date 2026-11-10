# LVL ESSENCE · Fase 1.3 — Búsqueda y paginación administrativa

## Punto de partida

Archivos preparados sobre `LVLEssence.zip` (6-7 de noviembre) más las fases **1.1 y 1.2** que el usuario ya instaló y probó. **No aplicar este paquete si falta la fase 1.1** (el servicio de búsqueda y los records `PaginaPerfumes`/`PerfumeResumen`).

Este ZIP **solo contiene seis archivos del proyecto y esta guía**; no contiene base de datos, credenciales ni `.git`.

## Instalación

1. Detén Spring Boot y guarda una copia de seguridad del proyecto. Para cualquier cambio de base de datos, respalda antes `data/` con H2 cerrado (esta fase NO cambia entidades ni esquema).
2. Extrae el ZIP en la raíz de `LVLEssence` (la carpeta que contiene `pom.xml`). Sobrescribe solo los archivos indicados.
3. Reinicia la aplicación y entra en `http://localhost:8080/admin/gestionarPerfumes` con la sesión administrativa iniciada.
4. Comprueba los filtros y la paginación. **No es necesario abrir H2 ni modificar datos**.

## Archivos del parche

- `src/main/java/com/leo/tienda/perfumes/controllers/ControladorPerfumes.java`: integra filtros en la página MVC y mantiene los parámetros al eliminar.
- `src/main/java/com/leo/tienda/perfumes/servicios/ServicioPerfumes.java`: añade una firma para recuperar por lote las entidades de la página.
- `src/main/java/com/leo/tienda/perfumes/serviciosImpl/ServicioPerfumesImplJPA.java`: recupera los 10 perfumes completos en una consulta y respeta el orden del buscador.
- `src/main/resources/templates/admin/producto/gestionar_productos.html`: formulario de búsqueda GET, tarjetas existentes, mensajes y enlaces de páginas; borrado POST con CSRF de Thymeleaf.
- `src/main/resources/static/css/estilos.css`: estilos administrativos, reutilizando el diseño de filtros/paginación pública.
- `src/test/java/com/leo/tienda/perfumes/GestionAdminPerfumesTest.java`: pruebas MockMvc con H2 en memoria.

## Qué hace

- Busca por **nombre** y filtra por **categoría**, **precio mínimo** y **precio máximo**.
- Ordena por **nombre ascendente/descendente**, **precio ascendente/descendente** y **más recientes**.
- Muestra **10 productos por página** y hasta **5 números de página visibles**.
- Conserva la ficha administrativa completa: imagen, marca, nombre, precio, familia, mililitros, concentración, género y descripción.
- Mantiene los enlaces `Editar` y formularios `Borrar` protegidos por POST/CSRF.
- Después de borrar, usa POST-redirect-GET: evita duplicar un borrado al refrescar y conserva búsqueda, filtros y página. Si la página ya no existe, salta a la última disponible.

## Pruebas manuales (datos actuales: 11 perfumes)

1. Abre `/admin/gestionarPerfumes` → muestra **10 perfumes**, total **11**, y enlace a página **2**.
2. En página 2 → muestra **1 perfume**.
3. Busca `Sauvage` → muestra solo ese producto.
4. Elige una categoría → filtra sus perfumes.
5. Pon `precioMax=50` → con el catálogo de prueba actual debería dar **0 resultados**.
6. Ordena por precio ascendente → empieza por el más barato.
7. Pulsa «Limpiar filtros» → vuelve al listado sin filtros, página 1.
8. Edita un perfume → el formulario debe seguir funcionando.
9. Intenta borrar un perfume asociado a un carrito o pedido → debe mostrar el bloqueo, sin error 500.
10. Comprueba que GET `/admin/borrarPerfume?id=...` devuelve 405 si hay sesión admin, y que sin sesión admin la vista sigue protegida.

## Tests automatizados

En la raíz del proyecto, con Maven configurado:

```
./mvnw -Dtest=GestionAdminPerfumesTest test
```

En Windows PowerShell:

```
.\mvnw.cmd "-Dtest=GestionAdminPerfumesTest" test
```

Estos tests usan `jdbc:h2:mem:admin_fase_1_3` y **no tocan** `data/base_de_datos.mv.db`.

**Estado de pruebas:** se revisaron los archivos y la estructura HTML. En este entorno no hay Maven instalado para ejecutar JUnit; hay que validar la integración en IntelliJ.

## Límites deliberados

- No se cambia `/restPerfumes/buscar`, `/restPerfumes/listar`, el catálogo público, el carrito ni el checkout.
- Hasta introducir ofertas, el filtro y el orden por precio utilizan el precio original `p.precio` (igual que en fase 1.1).
- No se cambia ningún esquema JPA ni se añaden columnas en H2.
