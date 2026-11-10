# LVL ESSENCE — Fase 1.1: backend de búsqueda y paginación

## Qué cambia

Se añade una API de solo lectura con **10 perfumes por página**. Los filtros son combinables:

- `texto`: parte del nombre del perfume (ignorando mayúsculas/minúsculas).
- `categoriaId`: identificador numérico de categoría (opcional).
- `precioMin` y `precioMax`: límites inclusivos de precio (opcionales).
- `orden`: `nombreAsc` (predeterminado), `nombreDesc`, `precioAsc`, `precioDesc`, `recientes`.
- `pagina`: comienza en **1**; predeterminada 1.

Ejemplos:

```
http://localhost:8080/restPerfumes/buscar
http://localhost:8080/restPerfumes/buscar?texto=sauvage&pagina=1
http://localhost:8080/restPerfumes/buscar?precioMin=70&precioMax=100&orden=precioAsc&pagina=1
http://localhost:8080/restPerfumes/buscar?categoriaId=1&orden=nombreDesc&pagina=2
```

La respuesta JSON tiene esta estructura:

```json
{
  "perfumes": [{"id": 1, "nombre": "Sauvage", "marca": "Dior", "precio": 95.0, "categoria": "Amaderados"}],
  "total": 1,
  "pagina": 1,
  "tamano": 10,
  "totalPaginas": 1
}
```

El ejemplo es ilustrativo: los IDs, categorías y totales reales dependen de tu H2.

## Compatibilidad

**No se toca** `/restPerfumes/listar`, ni `index.html`, ni la administración, ni las entidades JPA, ni los pedidos, ni `SeguridadConfig`.
Las plantillas actuales siguen usando el listado antiguo hasta la fase 1.2.
Los límites de precio usan por ahora el precio original `Perfumes.precio` (todavía no hay descuentos). El servicio centraliza su expresión para poder cambiarla en la fase de ofertas.

## Integración en tu proyecto

1. Detén Spring Boot y guarda una copia del proyecto y la carpeta `data/`.
2. Extrae **este ZIP dentro de la raíz de tu proyecto `LVLEssence`**; copia las rutas `src/...` y acepta sustituir **solo** estos tres archivos existentes:
   - `src/main/java/com/leo/tienda/perfumes/servicios/ServicioPerfumes.java`
   - `src/main/java/com/leo/tienda/perfumes/serviciosImpl/ServicioPerfumesImplJPA.java`
   - `src/main/java/com/leo/tienda/perfumes/controladoresREST/PerfumesREST.java`
3. Se crean dos clases en `responseData/` y una nueva prueba `BusquedaPerfumesTest.java`.
4. Ejecuta `mvn clean test` desde la raíz (o la ejecución Maven de IntelliJ), con una versión de Java compatible con Java 17.
5. Arranca Spring Boot, abre uno de los ejemplos y comprueba la respuesta JSON.

Los tests nuevos usan la base en memoria `jdbc:h2:mem:busqueda_perfumes`, por lo que no deben tocar `data/base_de_datos.mv.db`.

## Verificación en este entorno

- Se ha revisado el código fuente del ZIP actual y compilado las dos clases DTO con Java 17.
- **No se ha ejecutado la suite Maven completa**, porque en este entorno no está disponible Maven y el ZIP original no contiene `.mvn/wrapper/maven-wrapper.properties` necesario para el wrapper de Linux. Esto **no prueba un error en tu IntelliJ**; comprueba el resultado de los tests antes de integrar en producción.
- La base de datos original del ZIP no se ha modificado.

## Siguiente paso

Fase **1.2**: conectar `index.html` y `listado_productos.html` con `/restPerfumes/buscar`, crear controles de búsqueda, precio, categoría y paginación, con manejo de errores y estado vacío.
