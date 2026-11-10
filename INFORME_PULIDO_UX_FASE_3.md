# Pulido UX — fase 3: login y registro

## Archivos y composición

- `static/plantillas_html/identificar_usuario.html` y `registro_usuario.html`: formularios SPA con etiquetas, alternancia, obligatoriedad y botón de visibilidad de contraseña.
- `static/css/acceso-premium.css`: composición de dos columnas con imagen local `Sauvage.webp`, halo azul y superficie Liquid Glass; el panel decorativo se oculta por debajo de 768 px.
- `templates/index.html` e `i18n.js`: integración AJAX, foco, estados de envío, traducciones y carga de estilos. Se reutilizan los tokens y `LVLFeedback` existentes.

## Comportamiento

Se conservan los endpoints, nombres de campos, sesión, CSRF y actualización de usuario y favoritos. Las peticiones bloquean el botón mientras están pendientes y conservan los valores en caso de error. Las respuestas tardías no muestran mensajes en una vista que ya se haya desmontado. El registro correcto usa el sistema de notificaciones existente.

Los errores generales se muestran en el formulario. Un conflicto de correo se asocia al campo si el servidor proporciona un detalle que lo identifica; el backend utiliza HTTP 409 también para otras validaciones y puede no incluir el detalle, por lo que en ese caso se muestra un error general sin atribuirlo incorrectamente al correo. No se modificó el servidor.

La guía `ui-ux-pro-max` orientó etiquetas, autocompletado, visibilidad de contraseña, foco, objetivos táctiles y movimiento reducido. No se bloquea el pegado ni se añaden reglas de validación.

## Pruebas y limitaciones

- `node --check` correcto para `i18n.js` y el script principal extraído de `index.html`.
- `git diff --check` correcto con avisos de conversión de finales de línea.
- Maven no ejecutado: Spring Boot `Application` estaba activo con DevTools. Las pruebas de seguridad existentes usan H2 en memoria, pero ejecutarlas mientras la aplicación está activa incumpliría la condición solicitada.
- No se probaron las pantallas en navegador: quedan pendientes login y registro reales, errores, doble envío, cambio ES/EN, teclado, contraste y tamaños de 360, 768 y 1440 px.
- No se encontraron referencias locales de Gladia y el servicio Refero indicó que la suscripción no estaba activa; se siguió la descripción visual facilitada.
