# LVL ESSENCE — Fase 2: cambios de estado de pedidos

## Qué incorpora

- Selector de estado en `/admin/verDetallesPedido?id=...`, solo en pedidos finalizados.
- Formulario `POST /admin/actualizarEstadoPedido` con la protección CSRF existente de Spring Security y acceso restringido a administradores por `InterceptorAdmin`.
- Mensajes de confirmación/error, redirigiendo al detalle sin repetir el formulario al recargar (patrón POST/Redirect/GET).
- Validación en el **servicio JPA** además del formulario: una petición manipulada no permite completar un checkout ni devolver pedidos a `EnProceso`.
- Tres métodos de prueba JUnit con H2 en memoria: reglas de transición, servicio, permisos/CSRF y vista Thymeleaf.

## Transiciones permitidas

| Estado actual | Cambios desde administración |
|---|---|
| `EnProceso` | Ninguno. Solo `confirmarPedido()` lo pasa a `Completado` al finalizar el checkout. |
| `Completado` | `Enviado`, `Incidencia` |
| `Enviado` | `Incidencia` |
| `Incidencia` | `Completado`, `Enviado` |

Se permite repetir el mismo estado (operación sin cambios) para que una petición repetida sea inocua. No existe un estado `Entregado` en el proyecto, así que no se ha inventado.

## Instalación

1. **Detén Spring Boot** y guarda una copia del proyecto. Los archivos de base de datos no están incluidos en este ZIP.
2. Asegúrate de haber aplicado las fases 1.1, 1.2 y 1.3 que ya has probado.
3. Extrae este paquete en la raíz de LVLEssence (la carpeta con `pom.xml`), respetando los directorios `src/...`. Sobrescribe **solo** los archivos incluidos.
4. Ejecuta primero el test nuevo: `mvn -Dtest=EstadosPedidosAdminTest test` (o `./mvnw test -Dtest=EstadosPedidosAdminTest` según tu terminal).
5. Ejecuta después `mvn clean test`. En la versión anterior había 15 métodos JUnit, y el nuevo archivo añade 3 (esperado: 18, si todo está instalado y descubierto).
6. Arranca Spring Boot y abre `/admin/gestionarPedidos` > «Ver detalles». Prueba primero el pedido de ejemplo, que debería estar `Completado`.

## Validación manual

- En un pedido `Completado`, aparecen las opciones `Enviado` e `Incidencia`.
- Al guardar `Enviado`, se muestra confirmación y el estado persiste tras recargar.
- En un pedido `EnProceso`, no aparece el selector; un POST manual de cambio también debe rechazarse.
- Un usuario sin sesión admin no puede cambiar el estado.
- Una petición POST sin token CSRF devuelve HTTP 403.
- La página de listado refleja el estado actualizado.
- El checkout del cliente sigue terminando pedidos como `Completado`.

## Archivos incluidos

- `src/main/java/com/leo/tienda/perfumes/constantesPedidos/EstadosPedido.java`
- `src/main/java/com/leo/tienda/perfumes/serviciosImpl/ServicioPedidosImplJPA.java`
- `src/main/java/com/leo/tienda/perfumes/controllers/ControladorPedidos.java`
- `src/main/resources/templates/admin/pedidos_detalle.html`
- `src/test/java/com/leo/tienda/perfumes/EstadosPedidosAdminTest.java` (nuevo)

## Compatibilidad y límites

- No se modifica `Pedido.java`, ni la estructura de la base H2, ni el catálogo, ni las plantillas públicas.
- El formulario utiliza la integración de Thymeleaf con Spring Security para incluir automáticamente el campo CSRF de formularios POST (`th:action`).
- **No se ha podido ejecutar Maven en este entorno**: se ha compilado el enum aislado con `javac --release 17` y se ha comprobado la estructura de los archivos. Las pruebas de integración deben ejecutarse en tu entorno.
- El paquete se preparó sobre `LVLEssence.zip` (último ZIP completo disponible) y superpuso, en orden, los paquetes de las fases 1.1, 1.2 y 1.3. Si has realizado cambios locales adicionales en los cuatro archivos principales, compara sus diferencias antes de sobrescribir.
