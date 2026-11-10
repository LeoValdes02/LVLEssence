# LVL ESSENCE — Fase 1.2: interfaz de catálogo

## Requisitos previos

La fase 1.1 ya debe estar instalada. En concreto, el servidor debe responder a:

- `GET /restPerfumes/buscar` con `{ "perfumes": [...], "total": ..., "pagina": ..., "tamano": 10, "totalPaginas": ... }`.
- `GET /restPerfumes/listar` sigue existiendo (no se modifica en esta fase).

## Instalación

1. Detén la aplicación y guarda una copia de seguridad de tu código actual.
2. Extrae el contenido de este ZIP **en la raíz del proyecto LVLEssence**, donde está `pom.xml`, respetando las carpetas `src/...`.
3. Al extraer, sobrescribe solo los archivos que estén dentro de este ZIP; no se incluyen bases de datos, archivos `.git` ni `target/`.
4. Reinicia Spring Boot (si IntelliJ utiliza caché, haz *Rebuild Project*).
5. Abre `http://localhost:8080/` y pulsa **PERFUMES** en el menú. Esta fase no cambia todavía la página de inicio con Hero.
6. Opcionalmente, ejecuta los tests Maven: `./mvnw test` (macOS/Linux) o `mvnw.cmd test` (Windows).

## Qué cambia

| Archivo | Modificación |
| --- | --- |
| `src/main/resources/templates/index.html` | Nueva vista de catálogo y eventos AJAX para filtros y paginación. Conserva carrito, login, checkout y detalle. |
| `src/main/resources/static/plantillas_html/catalogo_filtros.html` | Nuevo formulario accesible con buscador, categoría, rango de precio, orden y controles de página. |
| `src/main/resources/static/css/estilos.css` | Estilos responsive del catálogo coherentes con la estética LVL ESSENCE. |
| `src/main/java/com/leo/tienda/perfumes/controladoresREST/PerfumesREST.java` | Conserva los endpoints previos y añade `GET /restPerfumes/categorias`. |
| `src/main/java/com/leo/tienda/perfumes/responseData/CategoriaFiltro.java` | Respuesta mínima `{id, nombre}` para poblar el selector. |
| `src/test/java/com/leo/tienda/perfumes/CategoriasCatalogoTest.java` | Prueba del endpoint de categorías con H2 en memoria. |

**No se han modificado** entidades, carrito, pagos, seguridad, esquema de H2 ni datos reales. La ordenación por precio utiliza el precio original del catálogo; las ofertas se implementarán en una fase posterior.

## Pruebas manuales (en navegador)

1. Pulsa **PERFUMES**: aparece el buscador, el selector de categoría, dos precios y el orden.
2. Sin filtros, la página 1 muestra hasta **10 tarjetas**, el total y la paginación (con los 11 perfumes de ejemplo, hay 2 páginas).
3. Pulsa **Siguiente**: aparece el perfume restante. Pulsar **Anterior** vuelve a la página 1.
4. Escribe `sauvage` y pulsa **Aplicar filtros**: se muestra Sauvage.
5. Indica precio máximo `50` y aplica: aparece el estado sin resultados (con los datos de ejemplo).
6. Pulsa **Limpiar**: vuelven todos los perfumes.
7. Elige **Amaderados** y aplica: se muestran solo perfumes de esa categoría.
8. Elige **Precio: menor a mayor**: se ordenan por precio ascendente.
9. Desde una tarjeta, pulsa **Ver detalles**, vuelve a **PERFUMES**, agrega al carrito y comprueba que el carrito y checkout siguen funcionando.
10. En móvil, comprueba que el formulario ocupa una columna y las tarjetas no se desbordan.

## Comprobación adicional

Visita `http://localhost:8080/restPerfumes/categorias`. Debe devolver una lista similar a:

```json
[{ "id": 1, "nombre": "Cítricos" }, { "id": 2, "nombre": "Florales" }]
```

Los IDs reales dependen de la base de datos. No introduzcas IDs inventados en el HTML.

## Notas de seguridad y arquitectura

- Las búsquedas son GET: no necesitan token CSRF; los POST de carrito y checkout mantienen su flujo y protección actual.
- La plantilla original de tarjetas utiliza campos MAYÚSCULOS; la nueva API responde con minúsculos. La adaptación se realiza solo para mostrar datos, sin modificar la API ni la plantilla del carrito.
- El formulario envía `categoriaId` (ID) en lugar del nombre visible: el servidor puede filtrar correctamente sin depender de tildes o textos.
- El formulario envía página 1 cuando cambian filtros; los botones de paginación hacen peticiones nuevas al backend, no filtran únicamente los productos cargados.
- No se ha ejecutado `mvn test` aquí porque Maven no está instalado en este entorno. La comprobación de sintaxis de JavaScript con Node y la compilación aislada del DTO `CategoriaFiltro` sí han pasado.
