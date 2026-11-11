# Informe de corrección visual: Inicio y administración

## Referencias inspeccionadas

- `referenciasDiseño/step - 1.jpg`: portada de Gladia con paneles grandes, composiciones asimétricas y bloques secundarios.
- `step - 2.jpg`, `step - 3.jpg` y `step - 4.jpg`: registro e inicio de sesión de Gladia.
- `step - 5.jpg`: consola de Gladia con navegación lateral y tarjetas.

Las cinco imágenes se inspeccionaron visualmente. No contienen capturas del Inicio ni de la administración de LVL ESSENCE; los fallos descritos se contrastaron con las reglas y la estructura del proyecto, no con capturas propias de la aplicación.

## Causas y correcciones

El escaparate se colocaba en una sola columna de `#contenedor`, que ya es una cuadrícula de columnas de 250 px; dentro de ella se dividía de nuevo en dos columnas. Ahora ocupa todo el ancho y utiliza paneles editoriales con columnas flexibles y apilado en pantallas estrechas.

La regla `.admin-seleccion-oferta input[type="checkbox"]` reducía la pista a 1,05 rem mientras el pseudo-elemento del interruptor estaba dimensionado para 2,75 rem y se desplazaba 1,2 rem. Se eliminó esa regla y se reservaron 2,75 rem para el control, con texto separado dentro de la misma etiqueta nativa. Se mantienen el formulario y la persistencia temporal existentes.

## Archivos modificados

- `src/main/resources/templates/index.html`: imágenes y composición de selecciones, iconos y etiquetas de navegación, estado de cuenta.
- `src/main/resources/templates/admin/producto/gestionar_productos.html`: separación estructural del texto del interruptor.
- `src/main/resources/static/plantillas_html/inicio.html`: fotografía del hero.
- `src/main/resources/static/plantillas_html/identificar_usuario.html` y `registro_usuario.html`: referencias al nombre actual de la imagen local de Sauvage.
- `src/main/resources/static/css/estilos.css` y `navegacion-premium.css`: cuadrícula editorial, tarjetas administrativas, iconos y etiquetas flotantes.
- `src/main/resources/static/javascript/i18n.js`: textos nuevos ES/EN.
- Este informe.

## Composición e imágenes

El hero utiliza `hero1.webp` con el texto y CTA en una columna y fotografía en la otra. Las selecciones usan `hero5.webp` para Amaderados, `hero4.webp` para Cítricos, `hero3.webp` para Orientales y `hero6.webp` para Último añadido. Son imágenes editoriales decorativas; las fotografías de productos identificados se cargan por `/mostrar_imagen` de manera independiente. Si falta una fotografía de producto, se oculta sin atribuirle el banner. Una categoría real sin productos conserva su sección y CTA.

Los archivos `hero1` a `hero6` se inspeccionaron visualmente; son WebP de 736×920, 735×985, 736×736, 1200×1500, 736×736 y 736×864 píxeles respectivamente. No se modificaron los originales. El hero recibe prioridad de carga y los banners inferiores carga diferida.

## Navegación y responsive

Se usan `search.png`, `user.png`, `corazon.png` y `shopping-bag.png`; se mantiene la marca tipográfica porque no se identificó un logotipo local específico. Las etiquetas de escritorio aparecen con hover o foco y no participan en el tamaño de la barra. El carrito móvil conserva su acceso propio. No se modificó la lógica de navegación compacta ni su histéresis.

El hero y las selecciones se apilan a 800 px; las tarjetas administrativas pasan a una columna en móvil y sus atributos se presentan en una columna. Se retiró el ancho mínimo global de 360 px. Se mantienen foco visible y el tratamiento existente de movimiento reducido.

## Pruebas ejecutadas

| Prueba | Resultado |
| --- | --- |
| `node --check` en `i18n.js` | Correcto |
| Sintaxis de scripts integrados en `index.html` | Correcta |
| `git diff --check` | Correcto, con avisos de finales de línea preexistentes |
| Revisión de especificidad y rutas gráficas modificadas | Realizada estáticamente |
| Maven | No ejecutado: puerto 8080 ocupado; no se ha detenido el proceso existente |
| Pruebas visuales y funcionales en navegador | No verificadas: no hay herramienta de navegador disponible |

## Verificaciones visuales pendientes

En un navegador, abrir Inicio a 360, 390, 768, 1024 y 1440 px; comprobar encuadre, ausencia de desbordamiento, CTA y filtros. En escritorio comprobar centrado del logo, hover, foco y scroll. En administración, a los mismos anchos, comprobar interruptor activo e inactivo, clic en etiqueta, selección múltiple, persistencia temporal, envío por lotes y visibilidad de Editar/Borrar. Verificar también login, búsqueda, favoritos, carrito e idiomas. No se han realizado commits.
