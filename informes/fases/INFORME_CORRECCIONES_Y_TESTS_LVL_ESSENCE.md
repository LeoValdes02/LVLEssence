# Informe de correcciones y tests — LVL Essence

Fecha: 6 de noviembre de 2026. Se han preservado los cambios que ya existían en el espacio de trabajo; no se han hecho commits ni despliegues.

## Resumen de correcciones

| Problema original | Archivos modificados | Cambio | Resultado y estado |
|---|---|---|---|
| Mutaciones administrativas por GET | ControladorPerfumes, ControladorCategorias, ControladorUsuarios; plantillas de gestión | POST para cambios; formularios de borrado con confirmación | GET de borrado de perfume devuelve 405 con sesión; resto de mapeos revisados estáticamente. Pruebas individuales pendientes |
| Riesgo CSRF | SeguridadConfig, pom.xml, index.html, plantillas Thymeleaf | Spring Security CSRF; token de formulario o cabecera AJAX; filtro multipart anterior a seguridad | MockMvc con filtro real rechaza token ausente o inválido y acepta token válido. Flujo multipart en servidor real pendiente |
| Pérdida de historial por borrado | ServicioPerfumesImplJPA, ServicioCategoriasImplJPA, controladores y plantillas | Rechazo explícito para productos asociados a pedidos o carritos y categorías con productos | PASS servicio para perfume asociado a pedido; otros casos y concurrencia pendientes |
| Sesión pública y login por GET | UsuariosREST, InterceptorAdmin, index.html | POST, rotación del ID, consulta de sesión sin contraseña, logout y navegación | PASS MockMvc de rotación, logout y DTO; refresco visual pendiente |
| Contraseñas de clientes en texto plano | Usuario, ServicioUsuariosImplJPA, ServicioSetUpImplJPA, editar_usuario.html, pom.xml | BCrypt con salt y columna nullable de versión; conversión individual tras login correcto; edición vacía conserva contraseña | PASS con cuenta antigua sintética, contraseña errónea, hashes distintos y edición sin cambio. No se han inspeccionado ni convertido cuentas reales |
| Precios históricos | ProductoPedido, ServicioPedidosImplJPA, ServicioSetUpImplJPA, ControladorPedidos, pedidos_detalle.html | Precio unitario decimal nullable; líneas antiguas usan precio actual con indicación de estimación | PASS cambio del precio del catálogo tras compra; visualización y datos antiguos reales pendientes |
| Errores del checkout y datos en consola | PedidosREST, index.html, check_out_2.html, estilos.css | Respuesta de error legible, función AJAX, pista de formato y retirada del registro del formulario | Validaciones y compra probadas con MockMvc anterior sin filtro; presentación visual pendiente |
| Consola H2 y wrapper | application.properties, .mvn/wrapper/maven-wrapper.properties, mvnw.cmd | Consola desactivada por defecto; restaurada URL Maven y corregido tratamiento de directorio no enlazado | `mvnw.cmd -version` PASS; consola opcional no probada |
| Tests conectables a desarrollo | ApplicationTests y tests nuevos | URLs H2 en memoria explícitas en cada contexto de test | PASS: logs muestran exclusivamente `jdbc:h2:mem:*` para las cuatro clases |

## Resultados de pruebas

Última ejecución: Maven 3.9.16, Java 22.0.2, `clean test` offline: **9 métodos JUnit ejecutados, 9 superados, 0 fallidos, 0 omitidos**. Se recompilaron 38 archivos Java con `release 17`. Evidencia en `target/surefire-reports/`: ApplicationTests 1, AuditoriaFinalTest 4, InicializacionAisladaTest 1 y SeguridadIntegracionTest 3. No son nueve casos de aceptación: algunos métodos agrupan numerosas comprobaciones.

- **Ejecutado:** validaciones de servicio, checkout MockMvc, carrito, confirmación, limpieza y bloqueo de borrado de usuarios con pedido; protección de rutas de consulta administrativas sin sesión; login y logout de ambos tipos; CSRF con filtro real; migración de contraseña sintética; separación de carritos; precio histórico; repetición de inicialización e independencia del método de ejemplo cuando la bandera está activada. La conservación de un pedido adicional se verificó en H2 aislada.
- **Limitaciones:** los tests anteriores `AuditoriaFinalTest` construyen MockMvc sin la cadena de filtros: sus resultados prueban controladores y servicios, no CSRF. La clase `SeguridadIntegracionTest` sí añade explícitamente la cadena. No se han ejecutado exhaustivamente todos los métodos y rutas administrativas, ni un reinicio real de proceso, ni pruebas de concurrencia, ni un arranque sobre una copia de una base antigua. No hay resultados de navegador automatizado; pendientes la presentación, mensajes, refresco y subida multipart real.
- **Bloqueado:** comparación de hash inicial de la base de desarrollo; el archivo estaba abierto por otro proceso al principio. No se ha comprobado el contenido de las cuentas reales ni la existencia de correos duplicados. No se ha aplicado restricción UNIQUE.
- Advertencias observadas: API Hibernate obsoleta y operaciones no verificadas en ServicioPerfumesImplJPA; `open-in-view`; sintaxis antigua de fragmentos Thymeleaf; carga dinámica de agente Mockito. Las respuestas 400/405 registradas durante tests corresponden a casos negativos esperados.

## Base de desarrollo y puesta en marcha

**No se puede afirmar que la base original no haya cambiado.** Al inicio `base_de_datos.mv.db` estaba bloqueado y medía 3.084.288 bytes con fecha 01:15:55; al finalizar medía lo mismo y mostraba 01:23:09. Se obtuvo entonces SHA-256 `1DA65D1A174E27C03D5549ED257BA6B063DB7678C166E77A7F68169FF8D2D6F1`, pero no hay hash inicial con el que compararlo. El proceso externo ya existía y podría reaccionar a cambios de compilación; no se ha determinado la causa del cambio de fecha. Los contextos de prueba registraron bases H2 en memoria, y no se han borrado archivos originales.

La configuración principal aún usa `ddl-auto=update`. **Antes de iniciar esta versión contra desarrollo**, detener procesos que tengan abierta esa base y realizar una copia de seguridad verificable; ensayar el arranque y el login antiguo sobre una copia aislada. El primer arranque podría añadir columnas para contraseña y precio. La conversión de una cuenta antigua se realiza al identificarse correctamente, no en bloque. Las líneas sin precio previo permanecen sin precio histórico y se muestran como estimaciones. La compatibilidad con la base real no está verificada.

## Archivos creados o modificados en esta intervención

- `pom.xml`, `.mvn/wrapper/maven-wrapper.properties`, `mvnw.cmd`, `src/main/resources/application.properties`: dependencias y configuración.
- `src/main/java/com/leo/tienda/perfumes/SeguridadConfig.java`: cadena CSRF, BCrypt y multipart.
- `src/main/java/com/leo/tienda/perfumes/controladoresREST/PedidosREST.java` y `UsuariosREST.java`: errores de checkout y sesión pública.
- `src/main/java/com/leo/tienda/perfumes/controllers/ControladorCategorias.java`, `ControladorPerfumes.java`, `ControladorUsuarios.java` y `ControladorPedidos.java`: métodos HTTP, errores de borrado y totales.
- `src/main/java/com/leo/tienda/perfumes/interceptores/InterceptorAdmin.java`: protección de `/admin`.
- `src/main/java/com/leo/tienda/perfumes/model/Usuario.java` y `ProductoPedido.java`: marca de contraseña y precio de línea.
- `src/main/java/com/leo/tienda/perfumes/serviciosImpl/ServicioCategoriasImplJPA.java`, `ServicioPerfumesImplJPA.java`, `ServicioUsuariosImplJPA.java`, `ServicioPedidosImplJPA.java` y `ServicioSetUpImplJPA.java`: integridad, contraseñas y precios.
- `src/main/resources/templates/index.html`, `admin/categoria/gestionar_categorias.html`, `admin/producto/gestionar_productos.html`, `admin/usuario/editar_usuario.html` y `admin/pedidos_detalle.html`: formularios, sesión y presentación.
- `src/main/resources/static/plantillas_html/check_out_2.html` y `static/css/estilos.css`: validación y estilo de checkout.
- `src/test/java/com/leo/tienda/perfumes/ApplicationTests.java`, `AuditoriaFinalTest.java`, `SeguridadIntegracionTest.java` e `InicializacionAisladaTest.java`: aislamiento y comprobaciones. Las dos últimas clases son nuevas en esta intervención; AuditoriaFinalTest procedía de la auditoría anterior.
- `INFORME_CORRECCIONES_Y_TESTS_LVL_ESSENCE.md`: este informe. `target/` contiene artefactos regenerados por Maven.

El estado Git también incluye cambios anteriores a esta intervención, incluidos archivos de base de datos y el informe previo; no deben atribuirse todos a este trabajo.

## Problemas pendientes

- **Críticos para Internet:** la contraseña administrativa `123` se conserva por instrucción expresa y permite acceso a quien la conozca. No publicar con esta configuración.
- **Altos:** faltan ensayo de actualización sobre copia de H2 real y verificación de datos, incluidos posibles duplicados; quedan cuentas antiguas en texto plano hasta su login correcto. La integridad de la base original no pudo compararse por hash. Revisar protección y límites del procesamiento multipart en un servidor real.
- **Medios:** falta restricción de unicidad transaccional del email; la comprobación actual puede sufrir una carrera. Faltan pruebas de reinicio real y de concurrencia; los pedidos antiguos solo disponen de estimaciones. Revisar `open-in-view` y cualquier ruta administrativa no cubierta individualmente.
- **Bajos:** advertencias de Hibernate, Thymeleaf y Mockito; pruebas de todas las operaciones negativas y consola H2 opcional pendientes.
- **Visuales:** comprobar manualmente checkout, navegación después de refrescar y formularios de administración, incluida subida de imágenes.

## Veredicto

**APTO CON OBSERVACIONES para evaluación académica local**, sujeto a probar la actualización en copia y realizar las verificaciones manuales pendientes. La suite aislada supera los casos ejecutados, pero no demuestra compatibilidad con los datos reales ni cobertura completa.

**No es seguro publicar en Internet:** la clave administrativa fija, cuentas antiguas sin migrar y comprobaciones de seguridad y despliegue pendientes requieren solución antes de una publicación.
