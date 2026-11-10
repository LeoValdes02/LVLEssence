# Rebranding 2.0 — fase 6: administración premium

## Archivos y componentes

- `templates/fragments/general.html`: marca y navegación administrativa compartida, retorno a tienda y formulario POST de cierre de sesión conservado.
- `static/javascript/admin-premium.js`: menú móvil, Escape, foco, sección activa y persistencia temporal de selecciones de oferta por página en `sessionStorage`; se limpian al cerrar sesión.
- `static/css/estilos.css`: estilos limitados a `.admin-page`, superficies Liquid Glass, navegación, tarjetas, controles, formularios y tablas; respaldo sólido y movimiento reducido.
- `templates/admin/producto/gestionar_productos.html`: tarjeta compacta, imagen contenida, stock, precio original tachado y checkbox nativo presentado como interruptor. No modifica su asociación al formulario de descuento por lotes.
- `templates/admin/pedidos_detalle.html`: región de tabla desplazable con indicación; `templates/admin/usuario/gestionar_usuarios.html`: acción de edición diferenciada de la eliminación.

## Funcionamiento y accesibilidad

El menú mantiene sus enlaces y el POST de logout con CSRF. En móvil se abre mediante botón con `aria-expanded` y `aria-controls`, dispone de cierre explícito y Escape, y devuelve el foco al botón. Sin JavaScript el menú permanece disponible. El interruptor representa selección, no aplicación inmediata de descuentos; la selección guardada es local a la pestaña y depende de la disponibilidad de `sessionStorage`.

Se consultaron las guías locales de `ui-ux-pro-max` para foco, contraste, responsive, estados y movimiento reducido; la identidad visual no se ha sustituido. El script opcional de la skill no pudo ejecutarse por falta de Python. La checklist visual requiere revisión en navegador.

## Pruebas y limitaciones

- `node --check static/javascript/admin-premium.js`: correcto.
- `git diff --check` sobre archivos rastreados afectados: sin errores, con avisos de finales de línea.
- `mvnw.cmd clean test`: **39 pruebas, 0 fallos, 0 errores y 0 omitidas**; H2 en memoria. Spring Boot estaba detenido al iniciarse la compilación. Incluye la aserción pendiente de fase 5 sobre familia olfativa.
- No se ha usado navegador: quedan pendientes las comprobaciones visuales desde 360 px, contraste, menú y foco con teclado/Escape, selección y recarga, tablas, formularios, imágenes, acciones y recorrido real de login/logout. Las pruebas automatizadas existentes no validan esas interacciones ni la correspondencia con las imágenes del PDF.
- No se han observado fallos en la suite, pero las comprobaciones visuales pendientes impiden descartar regresiones de presentación. La selección guardada no es compartida entre pestañas y deja de estar disponible si el navegador bloquea almacenamiento de sesión.
