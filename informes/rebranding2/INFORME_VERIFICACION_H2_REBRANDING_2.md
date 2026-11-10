# Verificación H2 persistente — Rebranding 2.0

**Veredicto: BLOCKED.** No se ha podido garantizar que la base original esté cerrada y que ninguna conexión la esté utilizando. La compatibilidad y la conservación de los datos no se han verificado.

## Comprobaciones previas

- Se localizó `C:\Users\Leo\IdeaProjects\LVLEssence\data\base_de_datos.mv.db`, de 4.214.784 bytes, con última modificación registrada el 09/11/2026 a las 05:06:17, y `base_de_datos.trace.db`, de 107.984 bytes, con última modificación el 08/11/2026 a las 23:10:17.
- La consulta de procesos efectuada fuera del aislamiento no mostró procesos llamados Java, Javaw o DBeaver. Sí mostró dos procesos de IntelliJ IDEA, con identificadores 17168 y 15112. No se ha comprobado que tengan una conexión H2; tampoco se dispone de una inspección fiable de todos los identificadores de archivo abiertos para descartarla.
- La ausencia de un archivo `.lock.db` no demuestra que la base esté cerrada. No se ha terminado ningún proceso ni eliminado ningún archivo de bloqueo.
- `application.properties` apunta a `jdbc:h2:file:./data/base_de_datos` y configura `ddl-auto=update`. `Application` invoca al arrancar los métodos de inicialización; estos pueden insertar registros de demostración según el estado de la base. DevTools figura en el proyecto.

## Operaciones pendientes

### Nueva comprobación

- La enumeración de procesos no mostró procesos llamados Java, Javaw o DBeaver; los procesos IntelliJ 17168 y 15112 seguían presentes. Su presencia por sí sola no demuestra una conexión a H2.
- `openfiles /query /fo csv /v` informó de que la opción del sistema necesaria para enumerar archivos locales abiertos no está habilitada y devolvió acceso denegado para la consulta de recursos compartidos. Por ello no permite descartar identificadores abiertos sobre la base.
- No se encontró `handle64.exe` en las búsquedas del proyecto, Descargas y Escritorio, ni `handle.exe` o `handle64.exe` en el PATH. No se ha podido realizar una inspección fiable de identificadores. No se han cambiado opciones del sistema.
- Los tamaños y fechas de los archivos H2 seguían coincidiendo con los registrados arriba; esto no prueba que la base esté cerrada ni que sus datos no hayan cambiado.
- También se identificó una llamada a `prepararRegistrosIniciales` desde `ControladorInicio`, además de las llamadas al arrancar. No se ha iniciado la aplicación para comprobar su efecto.

**Resultado de la nueva comprobación: BLOCKED.** Se requiere una herramienta que permita inspeccionar con alcance suficiente los identificadores abiertos del archivo original antes de copiarlo o conectarse a las copias.

- No se han calculado hashes ni realizado copias: la condición previa de cierre no está demostrada. No hay ubicaciones de copias que informar.
- No se ha abierto la base mediante JDBC, ejecutado Spring Boot, inspeccionado su esquema o sus registros, ni probado la galería. No se conocen las diferencias de esquema o datos ni el estado de las imágenes antiguas o nuevas.
- No se han realizado restauraciones, migraciones, modificaciones de código ni operaciones destructivas.

## Acción recomendada

Cerrar voluntariamente la aplicación y cualquier cliente que pueda acceder a la base, y comprobar con una herramienta adecuada los identificadores abiertos sobre los archivos de `data`. Repetir la verificación solo cuando pueda garantizarse el cierre; entonces obtener hashes, crear una copia de seguridad intacta y dos copias de trabajo independientes antes de cualquier conexión de diagnóstico.
