# Fase 7B — carrito y checkout

## Cambios

- `carrito.html`, `listado_productos.html`, `detalles_producto.html` e `index.html`: miniaturas, acceso al detalle, resumen de unidades y total, estado vacío y bloqueo temporal de algunas operaciones.
- `check_out_1.html`, `check_out_2.html` y `check_out_4.html`: autocompletado de envío y email, separación de facturación y pago simulado.
- `compra-premium.js` y `compra-premium.css`: indicador de cuatro pasos, apariencia responsive y foco visible. `i18n.js`: textos nuevos ES/EN.
- El tamaño no se muestra porque el endpoint del carrito no lo devuelve. El subtotal y total utilizan los subtotales finales del servidor; no se inventan gastos ni descuentos agregados.

## Límites y riesgos

- Los pasos se guardan en el pedido en proceso. No hay un mecanismo de lectura de formularios anteriores que permita volver a editarlos conservando los valores; no se añadieron enlaces de edición.
- La confirmación está dentro de una transacción, pero no se ha identificado bloqueo de concurrencia ni clave de idempotencia. Dos solicitudes simultáneas podrían competir; el botón deshabilitado no garantiza unicidad. Propuesta para una fase autorizada: clave de idempotencia asociada al pedido y bloqueo transaccional del pedido/carrito, con prueba de concurrencia y respuesta repetible.
- Las validaciones nativas y los mensajes existentes se conservan; aún faltan errores específicos junto a cada campo, indicadores de carga completos y verificación de bloqueos durante la carga de la siguiente plantilla. No se ha probado una confirmación concurrente real.
- La guía `ui-ux-pro-max` orientó foco, etiquetas, tamaño táctil, responsive y contraste. No se han medido todos los contrastes ni comprobado visualmente el fallback de vidrio.
- Pendiente comprobar con navegador carrito, detalle, cantidades, errores, doble clic, checkout completo, ES/EN, teclado, movimiento reducido y 360 px. No se han realizado pruebas visuales ni de interacción.

## Pruebas

- Sin procesos Java activos antes de Maven; las clases SpringBootTest declaran H2 en memoria.
- `mvnw.cmd clean test`: 39 pruebas, 0 fallos, 0 errores, 0 omitidas.
- `node --check` correcto para `compra-premium.js`, `i18n.js` y el script principal extraído de `index.html`.
- `git diff --check`: sin errores; Git avisó sobre conversiones de fin de línea en archivos existentes.
