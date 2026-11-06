# Informe de tests finales — LVL Essence

Fecha: 6 de noviembre de 2026. Alcance: archivos actuales del proyecto; no se ha modificado código productivo.

## A. Resumen ejecutivo

- Última ejecución: **5 tests JUnit, 5 superados, 0 fallidos, 0 omitidos**. Son métodos de test, varios con múltiples comprobaciones; no representan todos los casos solicitados. Hay casos **NOT_TESTED** en las tablas; el recuento de casos pendientes no es comparable con el de métodos JUnit.
- Compilación Maven: BUILD SUCCESS, pero Maven indicó que las clases estaban actualizadas; no se ha demostrado una compilación limpia. El wrapper falla porque falta `.mvn/wrapper/maven-wrapper.properties`; se utilizó Maven 3.9.16 instalado en la caché local.
- Riesgos principales detectados por inspección: contraseña administrativa fija y trivial, contraseñas públicas en texto plano, posibles peticiones CSRF y borrados accesibles por GET. No se han explotado estos problemas.
- Java configurado: 17; ejecución: Java 22.0.2. Spring Boot 4.1.1. JPA emplea EntityManager; el arranque detectó 0 repositorios Spring Data. Test existente: `ApplicationTests.contextLoads`.
- H2 de desarrollo apunta a `jdbc:h2:file:./data/base_de_datos`, con `ddl-auto=update`. Las pruebas usaron `jdbc:h2:mem:auditoria` y `jdbc:h2:mem:auditoriafinal`, confirmadas en logs de Hikari. La consola H2 está habilitada.
- El archivo original `.mv.db` estaba abierto por otro proceso y no permitió obtener su hash. Su hora de modificación pasó de 00:45:18 a 00:51:37 durante la auditoría; no es posible verificar su contenido ni atribuir el cambio. Las ejecuciones de Spring de esta auditoría registraron conexiones exclusivamente a las bases en memoria. No se borraron archivos de datos.
- No se ha localizado el enunciado original en el repositorio; el contraste de requisitos se limita a la lista facilitada.

Reproducción: usar Maven 3.9.16 y ejecutar `mvn -o "-Dspring.datasource.url=jdbc:h2:mem:auditoria;DB_CLOSE_DELAY=-1" test`. Es imprescindible mantener la sobrescritura: el test existente no aísla por sí solo la base. Evidencia: `target/surefire-reports/` y `AuditoriaFinalTest.java`. La ejecución generó avisos de agente dinámico Mockito y `open-in-view`; no fueron errores de test.

## B. Resultados

PASS significa comprobación ejecutada y superada; FAIL, ejecutada y fallida; BLOCKED, impedida por una limitación; NOT_TESTED, pendiente; REVIEW, observación estática. Las validaciones de servicio no equivalen a pruebas HTTP ni de navegador.

### 7.3 Checkout

| ID | Prueba | Resultado | Evidencia | Archivo implicado |
|---|---|---|---|---|
| 7.3.1 | Nombre vacío | PASS | Servicio lanza IllegalArgumentException | ServicioPedidosImplJPA.procesarPaso1 |
| 7.3.2 | Dirección vacía | PASS | Servicio lanza IllegalArgumentException | ServicioPedidosImplJPA.procesarPaso1 |
| 7.3.3 | Ciudad vacía | PASS | Servicio lanza IllegalArgumentException | ServicioPedidosImplJPA.procesarPaso1 |
| 7.3.4 | Postal tres cifras | PASS | Servicio rechaza 123 | ServicioPedidosImplJPA.procesarPaso1 |
| 7.3.5 | Postal cinco cifras | PASS | MockMvc acepta 28001, HTTP 200 | PedidosREST.procesarPaso1 |
| 7.3.6 | Email prueba@gmail | PASS | Servicio rechaza expresión inválida | ServicioPedidosImplJPA.procesarPaso2 |
| 7.3.7 | Email válido | PASS | MockMvc acepta prueba@example.com, HTTP 200 | PedidosREST.procesarPaso2 |
| 7.3.8 | Titular vacío | PASS | Servicio lanza IllegalArgumentException | ServicioPedidosImplJPA.procesarPaso2 |
| 7.3.9 | Sin elegir envoltorio | PASS | POST sin parámetro devuelve HTTP 400 | PedidosREST.procesarPaso3 |
| 7.3.10 | Datos válidos hasta resumen | PASS | POST de los tres pasos devuelve resumen JSON con email; no se probó visualización | PedidosREST.procesarPaso3 |
| 7.3.11 | Confirmar carrito vacío | PASS | Tras completar pasos, POST devuelve HTTP 409 | PedidosREST.procesarPaso4 |
| 7.3.12 | Confirmar sin pasos | PASS | POST devuelve HTTP 409 | ServicioPedidosImplJPA.confirmarPedido |
| 7.3-V | Navegador y mensajes visuales | NOT_TESTED | No se ejecutó navegador | check_out_1.html, check_out_2.html, check_out_3.html, index.html |

Por inspección, HTML usa `required`, `pattern` y `type=email`; JavaScript envía formularios por AJAX y evita mostrar resumen con carrito vacío. El backend valida texto, postal y email en los servicios, y el controlador exige una respuesta de envoltorio. Los casos inválidos anteriores se probaron directamente en el servicio salvo la ausencia de envoltorio; faltan pruebas HTTP independientes para cada entrada inválida.

### 7.4 Seguridad y sesiones

| ID | Prueba | Resultado | Evidencia | Archivo implicado |
|---|---|---|---|---|
| S1 | Administración sin sesión, perfumes y pedidos | PASS | MockMvc redirige ambas rutas | InterceptorAdmin.preHandle |
| S2 | Parámetro de contraseña en URL | PASS | GET a gestión con parámetro redirige | InterceptorAdmin.preHandle |
| S3 | Contraseña administrativa incorrecta | PASS | POST redirige a `?error=1` | ControladorAdmin.procesarLogin |
| S4 | Login admin POST y salida | REVIEW | Mapeos POST; se renueva ID al entrar y se elimina atributo al salir | ControladorAdmin |
| S5 | Acceso tras cierre admin | NOT_TESTED | No se ejecutó secuencia de login y logout | ControladorAdmin, InterceptorAdmin |
| S6 | Carrito sin sesión | PASS | GET consulta y POST borrado devuelven 401 | CarritoREST |
| S7 | ID de usuario desde sesión | REVIEW | Controladores leen `usuario_identificado`; no se probó sesión de dos usuarios | CarritoREST, PedidosREST |
| S8 | JSON sin contraseñas | NOT_TESTED | No se han cubierto todas las respuestas JSON | UsuariosREST, PerfumesREST, PedidosREST |
| S9 | Credenciales públicas incorrectas | PASS | Servicio devuelve null con contraseña ficticia incorrecta | ServicioUsuariosImplJPA.obtenerUsuarioPorEmailYpass |
| S10 | Refresco y salida pública | NOT_TESTED | No se ha probado navegador ni sesión posterior | index.html, UsuariosREST |
| S11 | Protección de todos los endpoints admin | NOT_TESTED | Dos rutas verificadas; no se ha enumerado y ejecutado cada ruta | Config, InterceptorAdmin |

### 7.5 Inicialización aislada

| ID | Prueba | Resultado | Evidencia | Archivo implicado |
|---|---|---|---|---|
| I1 | Base nueva: categorías, perfumes y pedido | PASS | Consultas JPA: al menos 4 categorías, 10 perfumes y 1 pedido | ServicioSetUpImplJPA.prepararRegistrosIniciales |
| I2 | Estado completado y dos líneas con cantidades positivas | PASS | Consultas JPA sobre pedido y líneas | ServicioSetUpImplJPA, ServicioPedidosImplJPA.confirmarPedido |
| I3 | Campos adicionales iniciales | PASS | Consulta verifica que los cinco campos no son null; no comprueba sus valores exactos | Pedido, ServicioSetUpImplJPA |
| I4 | Repetición sin duplicado | PASS | Reinvocar ambos métodos no cambia el número de pedidos | ServicioSetUpImplJPA |
| I5 | Reinicio real del proceso | NOT_TESTED | Se invocaron métodos de nuevo en el mismo contexto | Application.cargarDatosIniciales |
| I6 | Respeto de pedidos de usuarios | NOT_TESTED | No se preparó escenario independiente con pedido de usuario | ServicioSetUpImplJPA |
| I7 | `prepararPedidoEjemplo` independiente de la bandera | NOT_TESTED | Solo ejecutado cuando ya existía un pedido | ServicioSetUpImplJPA.prepararPedidoEjemplo |

### Regresión

| ID | Prueba | Resultado | Evidencia | Archivo implicado |
|---|---|---|---|---|
| R1 | Registro, email duplicado y credenciales correctas/incorrectas | PASS | Servicios JPA con usuario sintético y correo normalizado | ServicioUsuariosImplJPA |
| R2 | Añadir, sumar, modificar y borrar producto | PASS | Cantidades 2+1=3, después 4; borrado deja carrito vacío | ServicioCarritoImplJPA |
| R3 | Confirmar y vaciar carrito | PASS | HTTP 200 y consulta posterior vacía | PedidosREST, ServicioPedidosImplJPA |
| R4 | Conservar pedido completado | PASS | Consulta JPA encuentra el pedido tras confirmar; no prueba reinicio | ServicioPedidosImplJPA.confirmarPedido |
| R5 | Impedir borrar usuario con pedidos | PASS | Servicio lanza IllegalStateException | ServicioUsuariosImplJPA.borrarUsuario |
| R6 | Borrar usuario sin pedidos y carrito | PASS | Usuario y carrito ausentes tras borrado | ServicioUsuariosImplJPA.borrarUsuario |

### 7.6 Trazabilidad del enunciado facilitado

| Requisito | Implementación identificada | Verificación |
|---|---|---|
| Carrito, añadir, sumar, modificar y borrar | CarritoREST y ServicioCarritoImplJPA: agregarProducto, obtenerProductosCarrito, cambiarCantidad, borrarProducto; index.html | Servicios PASS R2; interfaz no probada |
| Registro y conservación de pedidos | PedidosREST.procesarPaso4; ServicioPedidosImplJPA.confirmarPedido | PASS R3–R4, sin reinicio |
| Pedido inicial | Application.cargarDatosIniciales; ServicioSetUpImplJPA.prepararRegistrosIniciales/prepararPedidoEjemplo | PASS I1–I4 con límites indicados |
| Checkout y campos adicionales | PedidosREST.procesarPaso1–4; ServicioPedidosImplJPA; Pedido; plantillas check_out_1–4 | PASS HTTP parcial 7.3.5, 7.3.7, 7.3.9–12; navegador pendiente |
| Listado y detalle administrativos | ControladorPedidos.gestionarPedidos/verDetallesPedido; gestionar_pedidos.html y pedidos_detalle.html | REVIEW: presentes; render autenticado no ejecutado |
| Campos adicionales en administración | Pedido y plantillas gestionar_pedidos.html/pedidos_detalle.html | REVIEW: referencias presentes; render no ejecutado |

## C. Hallazgos y recomendaciones

Los siguientes son **REVIEW**, no fallos demostrados mediante explotación. La prioridad refleja impacto potencial.

1. **Crítico — contraseña administrativa fija y trivial.** `ControladorAdmin.procesarLogin` compara con una constante en código. Inspeccionar el campo `passwordAdmin`; quien conozca el código o la clave puede obtener privilegios. Recomendar credenciales individuales robustas, almacenamiento seguro y configuración externa; no cambiar autenticación sin autorización.
2. **Alto — contraseñas públicas en texto plano.** `ServicioUsuariosImplJPA.registrarUsuario` persiste `Usuario.pass` y `obtenerUsuarioPorEmailYpass` compara el valor directamente. Inspeccionar ambos métodos. Una filtración de base revela credenciales. Recomendar hash adaptativo con migración de cuentas autorizada.
3. **Alto — posible CSRF y operaciones destructivas mediante GET.** `ControladorPerfumes.borrarPerfume` y `ControladorCategorias.borrarCategoria` usan `@RequestMapping` sin restringir método; no se ha encontrado protección CSRF en la configuración inspeccionada. Revisar mapeos y formularios; una sesión activa podría ejecutar solicitudes inducidas. Recomendar POST con protección CSRF en todas las mutaciones y verificación completa de rutas.
4. **Medio — sesión pública y credenciales en URL.** `UsuariosREST.identificarUsuario` usa `@RequestMapping` sin restringir método, acepta contraseña como parámetro y no renueva el identificador de sesión. Inspeccionar el método; GET podría exponer credenciales en historial o logs y existe riesgo de fijación de sesión. Recomendar POST, renovación de sesión y cierre público verificable. No se ejecutó prueba de explotación.
5. **Medio — datos personales en consola del navegador.** `index.html`, manejador de checkout 1, registra el formulario serializado. Inspeccionar `console.log("Datos enviados:", ...)`; incluye datos de envío. Recomendar eliminar dicho registro y revisar logs.
6. **Medio — precio histórico ausente.** `ProductoPedido` guarda perfume y cantidad; `ControladorPedidos.verDetallesPedido` calcula usando precio actual. Inspeccionar ambas clases: un cambio de precio podría alterar totales históricos. Recomendar guardar precio unitario al confirmar y planificar migración.
7. **Medio — consola H2 habilitada.** `application.properties` habilita `/h2-console`; comprobar protección y exposición en el despliegue antes de publicar. No se comprobó acceso de red ni explotación.
8. **Bajo — wrapper Maven incompleto.** `mvnw.cmd -version` falla por ausencia de `.mvn/wrapper/maven-wrapper.properties`. Recomendar restaurar los archivos del wrapper. La compilación con Maven local sí terminó correctamente, sin recompilación limpia.

## D. Mejoras pendientes no bloqueantes

- Mejorar CSS de los datos de envío y mensajes de validación del email.
- Mostrar el usuario público identificado después de refrescar; comprobar y revisar el cierre de sesión público y el acceso administrativo tras salir.
- Guardar el precio histórico del producto en cada pedido.
- Mejorar seguridad de contraseñas y gestión de sesiones, con la autorización correspondiente.

## E. Veredicto

**NO APTO TODAVÍA.** Las pruebas ejecutadas pasan y muestran funcionalidades básicas en H2 aislada, pero existen riesgos de seguridad de prioridad crítica y alta identificados en código y faltan verificaciones de sesiones, reinicio real, interfaz y cobertura HTTP completa. No se ha contrastado el enunciado original ni se ha podido verificar por hash la integridad del archivo de desarrollo abierto por otro proceso.

Archivos creados por esta auditoría: `src/test/java/com/leo/tienda/perfumes/AuditoriaFinalTest.java` e `INFORME_TESTS_FINALES_LVL_ESSENCE.md`. Maven también actualizó artefactos de compilación y resultados bajo `target/`. No se han realizado commits ni correcciones de producción.
