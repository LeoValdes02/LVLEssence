# Validación visual y funcional final — LVL ESSENCE

## Veredicto: NO APTO para aprobación visual

La suite automatizada pasa, pero no se ha podido realizar la validación visual y funcional en navegador. Este veredicto significa que faltan evidencias para aprobar la interfaz; no afirma que exista un defecto visual confirmado.

## Evidencias y pruebas realizadas

- Pantallas probadas en navegador: ninguna. Resoluciones y capturas: ninguna.
- No se encontró una herramienta de navegador automatizable ni Playwright instalado; no se instalaron dependencias ni se inició la aplicación con la base persistente.
- Antes de Maven no había una instancia Spring Boot activa; las clases de pruebas Spring declaran H2 en memoria.
- `mvnw.cmd clean test`: 39 pruebas, 0 fallos, 0 errores y 0 omitidas. Estas pruebas no sustituyen la validación de interfaz.
- `node --check` pasó para los archivos de `static/javascript` y los bloques JavaScript inline de `index.html`.
- Problemas confirmados en navegador y correcciones aplicadas: ninguno; no hubo prueba de navegador. No se modificó código frontend ni H2.
- `ui-ux-pro-max` se utilizó como referencia para la checklist de foco, contraste, formularios, tamaño táctil y responsive; no se afirma conformidad hasta comprobarlos.

## Checklist manual y capturas pendientes

Usar datos de prueba en una instancia aislada con H2 en memoria y DevTools desactivado. Tomar capturas a 360, 768 y 1440 px, y revisar a 1199 y 1200 px. Anotar navegador, resolución, pasos, resultado y consola para cada incidencia.

- [ ] Navegación: capturar cabecera normal y compacta; scroll lento y rápido, logo centrado, menú, cuenta, carrito e idioma.
- [ ] Catálogo: capturar tarjetas y filtros; comprobar imágenes, carrusel, swipe, ofertas, favoritos y cantidades.
- [ ] Detalle: capturar composición y miniaturas; comprobar halo, pseudo-3D, notas, valoraciones, precio y compra.
- [ ] Favoritos: capturar cuadrícula y vacío; comprobar eliminación, sincronización y ficha.
- [ ] Login y registro: capturar formulario y errores; comprobar alternancia, mostrar contraseña, carga y sesión.
- [ ] Buscador: capturar sugerencias y estado vacío; probar escritura rápida, respuestas tardías, flechas, Enter, Escape, clic y móvil.
- [ ] Carrito y checkout: capturar carrito y cada paso; verificar importes, errores, doble clic e indicador del paso 3.
- [ ] Administración: capturar tarjetas, tablas y formularios; comprobar galería, ofertas, CSRF y `sessionStorage` tras aplicar descuentos y cambiar de contexto.
- [ ] Notificaciones y diálogos: comprobar varios toasts, cierre, confirmación, cancelación, Escape y restitución del foco.
- [ ] En todas las pantallas: teclado, foco visible, contraste, movimiento reducido, imágenes ausentes, desbordamiento horizontal, consola, solicitudes duplicadas y anuncios de `aria-live`.

Clasificar cualquier problema observado como bloqueante, alto, medio o bajo y adjuntar captura o grabación. Las comprobaciones de rendimiento, lectores de pantalla y listeners repetidos también permanecen pendientes de ejecución real.
