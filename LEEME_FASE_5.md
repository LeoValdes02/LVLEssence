# LVL ESSENCE — Fase 5: idioma español / inglés

## Requisitos

- Haber instalado, en orden, las fases 1.1, 1.2, 1.3, 2, 3 y 4.
- La versión de partida es el `LVLEssence.zip` subido al proyecto, con los parches de las fases anteriores superpuestos.

## Instalación

1. Detener Spring Boot.
2. Guardar una copia de seguridad del proyecto (en particular `templates/index.html` y `static/css/estilos.css`).
3. Descomprimir este ZIP en la **raíz del proyecto**, al lado de `pom.xml`, conservando `src/main/resources/...`.
4. Reiniciar Spring Boot y abrir `http://localhost:8080/`.
5. Pulsar el botón **ES** / **EN** con icono de globo en la cabecera. Aparecerá un diálogo; seleccionar **English** o **Español**.
6. Si el navegador conserva CSS o HTML anteriores, hacer Ctrl+F5 (recarga forzada).

## Archivos incluidos

- `src/main/resources/templates/index.html`: botón del selector, diálogo accesible y enlace al nuevo script; adapta mensajes de alertas y el saludo del usuario.
- `src/main/resources/static/javascript/i18n.js`: diccionario ES→EN de textos de interfaz y observación de los fragmentos AJAX/Mustache; mantiene la preferencia en `localStorage`.
- `src/main/resources/static/css/estilos.css`: estilo del selector y del modal respetando el tema premium.
- `src/main/resources/static/plantillas_html/detalles_producto.html`: marca la descripción de cada perfume como dato del catálogo, que no se traduce automáticamente.

## Diseño técnico y alcance

- Solo se traduce **la interfaz pública**: cabecera, pie, Hero, catálogo, registro/login, carrito, checkout y páginas informativas creadas en la fase 4.
- Los textos legales se traducen como borradores: siguen requiriendo revisión jurídica antes de una tienda real.
- El panel de administración **continúa en español**. No se han modificado los controladores ni los endpoints REST.
- No se traducen automáticamente nombres/marcas/descripciones de perfumes ni nombres de categorías provenientes de H2. Para contenido bilingüe real del catálogo harían falta campos o traducciones en la base de datos.
- La selección queda guardada en el navegador (`localStorage`), no en la sesión del servidor. Es deliberado: el proyecto usa una página pública AJAX/Mustache y el sistema del profesor basado en `index_es`/`index_en` duplicaría páginas y scripts; mantenemos una sola vista.
- El idioma cambia al vuelo sin recargar el contenido actual ni borrar lo que el usuario haya escrito en el checkout.
- Sin migraciones H2, sin archivos de configuración Java modificados y sin cambios a CSRF, usuarios, descuentos ni pedidos.

## Comprobaciones manuales (aplazadas, por acuerdo)

1. Modal visible, botones ES/EN seleccionables, Escape lo cierra.
2. Cambiar a inglés: cabecera, Hero, filtros y navegación traducidos.
3. Ir a Contacto, About us, Privacy, Returns y Terms: contenidos en inglés y aviso legal conservado.
4. Entrar en registro, login, carrito y los cuatro pasos del checkout: etiquetas y mensajes en inglés.
5. Cambiar de idioma sin salir del checkout: no se borran los campos escritos.
6. Recargar: sigue el último idioma elegido.
7. Volver a Español: se restauran los textos originales.
8. Confirmar que nombres/precios/carrito/ofertas siguen igual.
9. Auditoría global de regresión JUnit + navegador: **pendiente para el cierre del proyecto**.

## Verificaciones hechas en la preparación del paquete

- Comprobación sintáctica de `i18n.js` y del JavaScript incrustado con `node --check`.
- **No se ha ejecutado Maven ni una prueba de navegador real**, según lo acordado para esta etapa.

## Nota de mantenimiento

Las nuevas frases visibles que se añadan a la tienda deben introducirse en el diccionario español/inglés de `javascript/i18n.js` para aparecer traducidas. Los datos dinámicos suministrados desde la base de datos son independientes del idioma de interfaz.
