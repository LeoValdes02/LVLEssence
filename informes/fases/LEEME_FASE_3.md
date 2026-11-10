# LVL ESSENCE · Fase 3 — Ofertas y precio final

**Base utilizada:** `LVLEssence.zip` actualizado (6 nov) + parches consecutivos de las fases 1.1, 1.2, 1.3 y 2. Los archivos incluidos son **un parche incremental**, no un proyecto completo. Instálalo únicamente si ya aplicaste las fases anteriores.

## Qué se añade

- Nuevo campo nullable `descuento_porcentaje` en `TABLA_PERFUMES`. Los perfumes previos, aun con `NULL`, se consideran sin descuento (0 %).
- En `admin/registrarPerfume` y `admin/editarPerfume`: campo de descuento de **0 a 100**, porcentaje entero; 0 elimina la oferta.
- En `/admin/gestionarPerfumes`: casillas de selección y formulario «Aplicar descuento» para los perfumes **marcados en la página actual**. El POST `/admin/aplicarDescuento` está protegido por el interceptor administrativo y el CSRF ya configurado; la operación es transaccional.
- Oferta calculada sobre el precio original: `precioFinal = precioOriginal × (100 - descuento) / 100`, redondeada a dos decimales. Ejemplo: Sauvage 95 € con 20 % → 76 €.
- Búsqueda, filtrado, ordenación y paginación usan el precio final para ser coherentes con las etiquetas de los perfumes.
- Las rutas REST existentes mantienen el campo de precio final y añaden precio original y porcentaje. Las tarjetas muestran precio anterior tachado y porcentaje solo si hay oferta.
- Carrito y resumen utilizan el precio final; el servidor guarda **ese importe** en `ProductoPedido.precioUnitario` al confirmar. Pedidos anteriores mantienen el precio ya almacenado, sin reescritura.
- Catálogo, ficha de perfume y carrito se actualizan periódicamente **cada 60 segundos** mientras se muestran y la pestaña está visible. Es una actualización periódica, **no tiempo real instantáneo**.

## Instalación (muy importante)

1. **Apaga completamente Spring Boot**, IntelliJ run y herramientas con H2 abierta.
2. Haz copia de seguridad de la **carpeta `data/` entera** y del proyecto (o crea un commit/punto de restauración sin incluir contraseñas ni la base).
3. Descomprime este ZIP en la raíz de LVL ESSENCE (donde está `pom.xml`) respetando las rutas `src/...` y acepta sobrescribir los archivos indicados.
4. Arranca Spring Boot. Como la aplicación utiliza `spring.jpa.hibernate.ddl-auto=update`, Hibernate debe agregar la columna `descuento_porcentaje` si todavía no existe. Si el arranque presenta errores de JPA/H2, **no manipules manualmente los datos**: recupera la copia o envía el error para revisarlo.
5. Abre `/admin/gestionarPerfumes`, inicia sesión y aplica, por ejemplo, 20 % a un perfume. Verás el precio final en administración, catálogo y carrito.

> Las pruebas automáticas se **han pospuesto a petición del usuario** hasta la auditoría final. En este entorno solo se ha verificado la sintaxis JS (`node --check`) y compilado los dos archivos Java puros con `javac`; **no se ha ejecutado Maven, Spring Boot ni una migración sobre H2**.

## Archivos incluidos

- `model/Perfumes.java`: campo, getters y precio final transitorio.
- `precios/PrecioPerfume.java`: operación monetaria central con BigDecimal.
- `responseData/PerfumeResumen.java`: DTO con precio final, original y porcentaje.
- `constantesSQL/ConstantesSQL.java`: detalles y lista antigua con oferta.
- `servicios/ServicioPerfumes.java` y `serviciosImpl/ServicioPerfumesImplJPA.java`: filtros por precio final y aplicación masiva de ofertas.
- `serviciosImpl/ServicioCarritoImplJPA.java`: precio final y subtotales.
- `serviciosImpl/ServicioPedidosImplJPA.java`: fotografía monetaria al confirmar.
- `serviciosImpl/ServicioSetUpImplJPA.java`: precio de línea para el pedido ejemplo recién creado.
- `controllers/ControladorPerfumes.java`: endpoint POST administrativo por lotes.
- `templates/admin/producto/registro_producto.html` y `editar_producto.html`: campo de porcentaje.
- `templates/admin/producto/gestionar_productos.html`: selector de perfumes y precio final.
- `templates/index.html`, plantillas públicas de listado, detalle, carrito y checkout: ofertas y refresco periódico.
- `static/css/estilos.css`: presentación del descuento.

## Comprobaciones pendientes para el final (no ejecutadas ahora)

- Arranque sobre copia de base H2 con perfumes anteriores; `descuento_porcentaje=NULL` debe equivaler a 0 %.
- Alta/edición individual y aplicación de descuentos 0, 20, 100; rechazos de porcentajes fuera de rango.
- Ofertas solo para IDs seleccionados, con POST y CSRF; nunca por GET o usuario público.
- Catálogo, detalle y carrito muestran el importe y el porcentaje correctos; ordenación y filtros por **precio final**.
- Cambio de oferta mientras el catálogo permanece abierto: se refleja al cabo de 60 segundos aproximadamente.
- El checkout utiliza el precio final vigente en el momento de confirmar. Los pedidos antiguos no modifican su importe histórico.
- Suite completa de regresión (catálogo, usuarios, admin, pedidos y carrito) en H2 en memoria.

## Límites conocidos

- Los precios originales se almacenan actualmente como `double` por compatibilidad con el proyecto. Se redondea el resultado a céntimos con `BigDecimal`; una futura migración a decimal persistido sería más rigurosa.
- El resumen de checkout no se refresca automáticamente mientras el cliente lo está leyendo; si el administrador cambia una oferta **justo entre visualizar el resumen y confirmar**, el precio guardado será el vigente al confirmar. Antes de una tienda real habría que congelar el precio ofrecido o solicitar confirmación cuando cambie.
- Las vistas se refrescan cada 60 s, no reciben eventos push.
- El sistema de ofertas es un porcentaje simple por perfume, sin fechas programadas ni cupones; estos no se han solicitado.
