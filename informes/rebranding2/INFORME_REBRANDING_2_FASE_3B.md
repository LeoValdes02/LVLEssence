# Rebranding 2.0 — fase 3B

## Cambios

- `listado_productos.html`, `estilos.css` e `index.html`: tarjetas Liquid Glass, imagen centrada, controles de galería, corazón, cantidad etiquetada y carga del script.
- `galerias-catalogo.js`: consulta agrupada, carrusel, swipe, fallback y estado de favoritos; `navegacion-premium.js` conserva los eventos y añade retirada desde el corazón. `i18n.js` incorpora etiquetas del carrusel.
- `PerfumeResumen` y `ServicioPerfumesImplJPA`: familia olfativa en el resultado de búsqueda, con constructor anterior conservado para los demás consumidores.

## Galería y rendimiento

La portada y las imágenes adicionales proceden de `/restPerfumes/galerias`, en orden. Se agrupan los IDs sin información y se reutilizan respuestas y peticiones pendientes durante la sesión de página. Las tarjetas visibles recuperan su posición durante el refresco; al dejar de estar visibles se descarta. La navegación es circular y no automática; las imágenes usan carga diferida y ajuste `contain`. Sin imágenes se muestra texto alternativo. Si falla la consulta se mantiene la portada existente como respaldo y se volverá a intentar al refrescar.

Los controles usan botones, etiquetas y foco existente, y la transición respeta movimiento reducido. El swipe no cancela el desplazamiento vertical. Los eventos del carrusel están delegados y se registran una sola vez.

## Comprobaciones realizadas

- `mvnw.cmd test`: 39 pruebas, 0 fallos, 0 errores, 0 omitidas; H2 en memoria.
- `node --check` de los scripts de navegación e internacionalización y del nuevo script: sin errores, incluido el ajuste final del manejador de imagen.
- `git diff --check` de los archivos rastreados principales: sin errores.
- Se aplicaron las pautas locales de la skill a semántica, foco, controles táctiles, estados, responsive y movimiento reducido. Su script de búsqueda no pudo ejecutarse por falta de Python.

## Pendiente de comprobación

No se dispuso de navegador: no se han comprobado visualmente las referencias del PDF, el diseño a 360 px y escritorio, contraste compuesto, imágenes ausentes o dañadas, carrusel circular, swipe, foco y teclado, estados de favoritos, carga y errores, idiomas, ni recorridos de carrito, filtros y paginación. Tampoco se han medido las peticiones ni detectado duplicaciones en ejecución. Las pruebas Maven existentes no sustituyen estas comprobaciones ni prueban la nueva interacción JavaScript. La compatibilidad de H2 persistente no se ha auditado en esta fase.
