# Rebranding 2.0 — fase 7: animaciones y auditoría UX

## Resultado y archivos

**Veredicto: APTO CON OBSERVACIONES**, condicionado a las comprobaciones pendientes; no equivale a aprobación visual ni a una auditoría completa en navegador.

- `static/javascript/animaciones.js` y `static/css/estilos.css`: se reutilizan un IntersectionObserver y un MutationObserver compartidos. El contenido ya no permanece oculto mientras espera intersección; al entrar se anima una sola vez con opacidad y desplazamiento leves. El foco hace visible el contenido y los nodos retirados se dejan de observar.
- `static/javascript/galerias-catalogo.js`: una página recién renderizada vuelve a suscribirse a peticiones de galería ya iniciadas y comprueba la versión de navegación antes de pintar.
- `templates/index.html`: desplazamientos programados respetan movimiento reducido y el fallo de imagen de detalle deja de actuar sobre vistas que ya no existen.
- `INFORME_REBRANDING_2_FASE_7.md`: este informe. No se cambiaron dependencias, Java ni H2.

El pseudo-3D existente conserva su propia transformación y limpieza al abandonar la vista. No se añadió parallax adicional. Las transiciones nuevas usan opacidad y transform; el CSS de revelado solo se activa sin movimiento reducido. Sin observadores o sin JavaScript, el contenido no queda oculto por el revelado; la SPA sigue dependiendo de JavaScript para cargar sus vistas.

## Checklist UX y accesibilidad

Se aplicaron las guías locales de `ui-ux-pro-max` a semántica, nombres de controles, foco, movimiento reducido, interacción táctil, formularios, rendimiento y superficies con respaldo sólido. El script opcional no pudo usarse porque Python no estaba instalado; no se añadieron herramientas.

- **Revisado estáticamente:** estructura de menús público y administrativo, etiquetas y atributos ARIA, botones de galerías, formularios de checkout, estilos de foco, fallback glass, escucha de cambios y limpieza de nodos observados.
- **Pendiente de verificar en navegador:** orden y visibilidad real del foco, Escape, estados dinámicos, contraste compuesto, tamaños táctiles, lectores de pantalla, zoom, preferencias de movimiento, desplazamiento y ausencia de desbordamientos.
- Los informes de fases anteriores no constituyen validación visual. El informe de fase 5 señala Maven pendiente, pero la fase 6 sí ejecutó posteriormente 39 pruebas, incluida la aserción de familia olfativa.

## Hallazgos

| Prioridad | Estado | Hallazgo y evidencia |
|---|---|---|
| Medio | Corregido; pendiente de navegador | El revelado dependía de intersección para dejar de ocultar elementos; se cambió a contenido visible por defecto y revelado de una sola vez. |
| Medio | Corregido; pendiente de navegador | Un nuevo render de catálogo podía compartir una petición de galería sin recibir su resultado; se conecta a las peticiones pendientes y se verifica la versión. |
| Medio | Corregido; pendiente de navegador | La imagen de una ficha ya retirada podía buscar un elemento de otra vista al fallar; se conserva la referencia de la ficha y se comprueba su conexión. |
| Medio | Corregido; pendiente de navegador | Dos desplazamientos explícitos solicitaban suavizado incluso con movimiento reducido; ahora consultan la preferencia. |
| Medio | Pendiente | No se han medido contraste, responsive, foco ni interacciones en navegador. El contenedor principal completo usa `aria-live`, por lo que debe comprobarse si anuncia cambios excesivos con lector de pantalla. |
| Bajo | Pendiente | La correspondencia visual con las referencias del PDF y la fluidez real del pseudo-3D no se han verificado. |

## Pruebas reales

- `node --check` de animaciones, galerías, detalle, navegación y administración: correcto. Los dos bloques JavaScript inline de `index.html` se analizaron con Node sin errores de sintaxis.
- `git diff --check` sobre archivos rastreados modificados de esta fase: sin errores.
- Maven **no ejecutado**: el proceso 11348 ejecutaba `com.leo.tienda.perfumes.Application` con DevTools. No se detuvo ni se compiló sobre la aplicación activa. La referencia previa sigue siendo 39 pruebas superadas en fase 6; no demuestra los cambios de esta fase.
- No se encontró herramienta de navegador automatizado ni ejecutable en las ubicaciones comprobadas. No se ejecutaron pruebas visuales, de consola, red, teclado, swipe ni rendimiento; no se afirma que ninguna pantalla esté visualmente aprobada.

## Checklist manual para cerrar

Con la aplicación detenida, ejecutar una vez `mvnw.cmd clean test` y registrar el resultado. Para las pruebas de navegador, arrancar en un entorno aislado H2 en memoria, no sobre `data/base_de_datos`.

1. A 360, 768 y 1440 px revisar cabecera, búsqueda, cuenta, idioma, catálogo, detalle, favoritos y administración; comprobar que no haya contenido cortado ni scroll horizontal inesperado.
2. Con teclado recorrer enlaces y formularios, abrir/cerrar ambos menús y el diálogo de idioma con Escape, verificar retorno de foco, etiquetas, estados deshabilitados y que la cabecera fija no oculte el foco.
3. Probar filtros y paginación rápidos, galerías con cero/una/varias imágenes, flechas y swipe; alternar detalle/favoritos y verificar ausencia de duplicados, errores de consola y peticiones fallidas inesperadas.
4. Probar login/logout, favoritos, cantidades, ofertas, carrito y checkout simulado; comprobar estados de carga/error y traducción ES/EN. Verificar interruptor de selección de oferta tras recarga y formulario administrativo de galería.
5. Repetir con movimiento reducido, imágenes fallidas y sin soporte de `backdrop-filter`; comprobar que los contenidos permanezcan visibles, accesibles y legibles. Revisar contraste con las superficies reales y lector de pantalla.

| Área | Estado | Evidencia | Pendiente |
|---|---|---|---|
| Movimiento | Parcial | Código y sintaxis revisados | Scroll, foco y preferencia real en navegador |
| Navegación y catálogo | Parcial | Inspección estática y corrección de galería | Interacciones, responsive y consola |
| Detalle y favoritos | Parcial | Inspección de lógica y fallback | Galería, sesión, compra y navegación real |
| Administración | Parcial | Fragmento y script inspeccionados | Menú, formularios y flujo de oferta |
| Accesibilidad e idiomas | Parcial | Etiquetas y reglas inspeccionadas | Contraste, teclado, lector de pantalla y ES/EN |
| Regresión | Bloqueada | Última suite de fase 6: 39 pruebas | Ejecutar suite de fase 7 con Spring Boot detenido |
