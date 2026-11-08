# LVL ESSENCE — Fase 6: animaciones y acabado premium

Este paquete **se aplica sobre el proyecto que ya tenga instaladas las fases 1 a 5**.
No contiene base de datos, no añade dependencias y no modifica Java ni endpoints.

## Archivos incluidos

1. `src/main/resources/templates/index.html`: carga el nuevo script.
2. `src/main/resources/static/css/estilos.css`: animacion de entrada, menu de cuenta, modal y detalles de hover/foco.
3. `src/main/resources/static/javascript/animaciones.js`: observa contenido AJAX, tarjetas y preferencias de accesibilidad.

## Instalacion

1. Detener Spring Boot y hacer una copia de seguridad del proyecto.
2. Extraer el ZIP en la carpeta raiz de `LVLEssence` (donde esta `pom.xml`), respetando las rutas `src/...`.
3. Sobrescribir los dos archivos existentes; `animaciones.js` es nuevo.
4. Arrancar la aplicacion y pulsar Ctrl+F5 para evitar CSS/JS en cache.

**Nota de version:** el `index.html` y `estilos.css` se basan literalmente en el ZIP entregado para la fase 5. Si has cambiado esos archivos manualmente desde entonces, compara antes de sobrescribir o integra los cambios puntuales.

## Resultado esperado (comprobacion visual, SIN suite automatica por ahora)

- Portada: el texto del Hero aparece suavemente al cargar.
- Al bajar: cabecera de catalogo, filtros y tarjetas van apareciendo de forma progresiva.
- Buscar `Sauvage`, cambiar la categoria o pasar a pagina 2: las tarjetas nuevas tambien se animan.
- Al volver al inicio, siguen funcionando Hero, carrito, ofertas y cambio de idioma ES/EN.
- El modal de idiomas y el desplegable de cuenta aparecen de forma suave.
- Con el sistema operativo en «Reducir movimiento», las tarjetas se muestran SIN animacion.
- Con JavaScript desactivado, estos nuevos estilos no ocultan el contenido ya renderizado (aunque la SPA existente depende de JavaScript para cargar su contenido).
- El tabulador permite distinguir el foco de los botones del catalogo.

## Alcance y limitaciones

- No se ejecuta JUnit ni la suite completa en esta fase, siguiendo lo acordado; la auditoria global es el paso posterior.
- El script actua solo sobre `#contenedor` de la tienda publica. No cambia la administracion.
- Los descuentos no se recalculan aqui; se reutiliza la fase 3.
- El efecto no se aplica a cada campo de los formularios para evitar interferir con el checkout.
- Animaciones deliberadamente discretas: no se usa paralaje, librerias externas ni scroll-jacking.

## Validacion tecnica realizada al crear el paquete

- `node --check` sobre `animaciones.js`.
- Se confirma que `index.html` enlaza `/javascript/animaciones.js` y que el ZIP contiene ese recurso.
- Se verifica que la hoja de estilo es una ampliacion de la fase 5 y mantiene las reglas anteriores.

**Siguiente paso:** una vez verificada manualmente la visualizacion, ejecutar la auditoria total de fases 1-6 y la suite Maven sobre H2 en memoria, con pruebas especificas de ofertas, sesion y cambios de estado.
