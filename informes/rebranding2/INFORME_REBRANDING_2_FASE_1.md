# Rebranding 2.0 - fase 1: Liquid Glass

## Archivos modificados

- `src/main/resources/static/css/estilos.css`: variables comunes, superficies, reflejos, botones, inputs y componentes reutilizables.
- `src/main/resources/static/css/navegacion-premium.css`: superficies de navegacion, busqueda, menu y favoritos con los mismos tokens.
- `src/main/resources/static/css/detalle-premium.css`: ajustes localizados de superficies, foco y boton, conservando la composicion.
- `INFORME_REBRANDING_2_FASE_1.md`: este informe.

## Variables y componentes

La paleta conserva los seis azules existentes. Se centralizaron `--glass-solid`, `--glass-raised`, transparencias, `--glass-blur`, borde, reflejo, sombra interior, hover y foco, reutilizando radios, espacios y transiciones anteriores. Las clases opcionales `.glass-panel`, `.glass-chip`, `.glass-icon-button`, `.glass-switch` y `.glass-input` quedan disponibles; los botones principales y secundarios siguen usando `.btn-primary` y `.btn-secondary`. El interruptor se aplica a un input nativo y respeta movimiento reducido.

## Cambios visuales

Las tarjetas y formularios comparten reflejos discretos y sombras interiores. La cabecera, el menu y el modal disponen de fondo oscuro solido como respaldo y desenfoque limitado cuando el navegador lo soporta. Se ajustaron busqueda, favoritos y controles de la ficha a los mismos colores y superficies. Se conservaron los colores semanticos y los estados de foco existentes. No se han cambiado HTML, JavaScript, servicios ni datos persistentes.

## Pruebas

- `mvnw.cmd test`: 35 pruebas ejecutadas, 35 superadas, 0 fallos, 0 errores y 0 omitidas. Los tests usan H2 en memoria.
- Revision automatizada de delimitadores, cadenas y comentarios en las tres hojas: superada. No habia un analizador CSS completo disponible, por lo que no equivale a una validacion integral del CSS.
- Revision de referencias a variables: `--lvl-delay` se define dinamicamente por JavaScript y dispone de valor alternativo en su uso. Las demas variables referenciadas se encontraron declaradas.
- `git diff --check` no informo de errores; aviso sobre finales de linea en la traza H2 preexistente.
- No habia navegador automatizado disponible. No se han abierto las pantallas ni comprobado visualmente escritorio, movil de 360 px, contraste, foco, movimiento reducido o fallback en navegadores reales.

## Conflictos y limitaciones pendientes

`estilos.css` se carga antes que `detalle-premium.css` y `navegacion-premium.css` en la tienda; estas hojas pueden prevalecer por orden y especificidad. La administracion utiliza solo `estilos.css`. Se mantiene la regla previa con `!important` para elementos ocultos del navegador, necesaria para respetar sus estados actuales; no se han anadido nuevas reglas con `!important`. Las clases optativas aun no estan conectadas a nuevos controles y deben comprobarse visualmente antes de su uso. Persisten reglas antiguas que no se eliminaron por falta de verificacion completa de uso.

No se realizaron commits ni migraciones. La comprobacion visual debe confirmar especificamente el aspecto en 360 px y que los efectos glass no dificulten la lectura.
