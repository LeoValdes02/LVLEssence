# Rebranding 2.0 — fase 4: detalle premium

## Archivos y composición

- `static/plantillas_html/detalles_producto.html` y `static/css/detalle-premium.css`: panel Liquid Glass con información, precio y compra; escena oscura con halo azul, frasco y miniaturas; notas y formulario de valoración. En móvil la escena se reorganiza verticalmente.
- `static/javascript/detalle-premium.js` e `index.html`: inicialización y limpieza de galería, cantidad y movimiento; integración con la vista y los precios existentes.
- `galerias-catalogo.js`: acceso compartido a la caché y a peticiones pendientes de la fase 3B. `navegacion-premium.js`: estado de favoritos y protección de respuestas de valoración. `i18n.js`: nuevas etiquetas ES/EN.

## Datos y comportamiento

La ficha usa descripción, tamaño, concentración, precio final, oferta y notas devueltos por el servidor. El tamaño es informativo. Se mantienen la validación de cantidad, el carrito y su contador, CSRF, sesión y valoraciones. El estado de favoritos se consulta al abrir y permite añadir o retirar; si falla su consulta se comunica el error.

La galería reutiliza `/restPerfumes/galerias`: miniaturas con botones seleccionables, swipe y fallback sin imágenes. Si falla la consulta permanece la portada. El movimiento es una aproximación CSS de una fotografía plana: inclinación limitada, flotación y profundidad leve con scroll en dispositivos con ratón; no usa sensor ni modelo 3D. Movimiento reducido lo desactiva. Los eventos asociados se liberan al cambiar de vista.

Se usaron las guías locales de `ui-ux-pro-max` para controles, foco, responsive y movimiento reducido, conservando la identidad. El script opcional no estaba disponible por falta de Python. La comprobación visual de su checklist queda pendiente.

## Pruebas y límites

- `mvnw.cmd test`: 39 pruebas, 0 fallos, 0 errores, 0 omitidas; H2 en memoria. Spring Boot estaba detenido; el único proceso Java identificado era el compilador de IntelliJ. La suite se ejecutó una vez antes de los últimos ajustes de presentación y del estado de carga de favoritos.
- `node --check`: scripts modificados sin errores después del último ajuste de JavaScript. La plantilla fue analizada con Mustache incluido en el proyecto. `git diff --check` no detectó errores; informó avisos de finales de línea.
- No hay navegador automatizado disponible: no se han verificado visualmente las imágenes de referencia del PDF, contraste, distribución desde 360 px, ni funcionamiento real de miniaturas, swipe, cantidad, ofertas, favoritos, valoración, carrito, idiomas, movimiento reducido, cambios rápidos de producto o fugas de listeners. La suite existente no prueba esas interacciones.
- No se ha probado la base persistente ni realizado una auditoría de compatibilidad. No se identificaron regresiones en la suite, pero las comprobaciones de navegador pendientes impiden descartarlas en la interfaz.
