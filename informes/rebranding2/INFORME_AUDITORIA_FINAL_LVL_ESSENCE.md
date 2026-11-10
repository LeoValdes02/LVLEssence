# Auditoria final - LVL ESSENCE

## 1. Resumen ejecutivo

Estado propuesto: APTO CON OBSERVACIONES para entrega academica local, condicionado a que se informe al evaluador de las comprobaciones pendientes. La suite automatizada pasa, pero no se ha verificado la actualizacion sobre los datos persistentes ni las pantallas en navegador. No es seguro publicarlo en Internet con la configuracion y credenciales actuales.

## 2. Resultados automatizados

- `mvnw.cmd clean test`: BUILD SUCCESS. Surefire registra 35 metodos ejecutados en 14 clases: 35 superados, 0 fallidos, 0 errores y 0 omitidos.
- Antes de ejecutar se inspeccionaron los tests: los 14 contextos Spring usan URL `jdbc:h2:mem:` distintas. No se encontro conexion de test a la base persistente.
- La suite incluye pruebas de sesion, BCrypt y migracion de contrasenas antiguas, CSRF, filtros y paginacion, descuentos, carrito, checkout, precios historicos, estados de pedidos, favoritos, valoraciones, concurrencia y recomendaciones. La cobertura de cada recorrido no equivale a una prueba visual completa.
- La prueba de ofertas verifica un precio guardado despues de retirar el descuento; la de auditoria recorre los cuatro pasos por HTTP con datos aislados. Las pruebas de favoritos comprueban aislamiento y CSRF; la de valoraciones comprueba media, actualizacion y concurrencia. No se han ejecutado los recorridos completos con navegador.
- Node compilo sintacticamente los cuatro scripts propios y los bloques inline de `index.html`. Esto no verifica su comportamiento en tiempo de ejecucion. `git diff --check` no informo de errores; emitio un aviso de finales de linea en la traza de H2 ya modificada.
- Maven emitio advertencias sobre API obsoleta, operaciones sin tipado, carga dinamica de agente Mockito y expresiones antiguas de fragmentos Thymeleaf. No causaron fallos en esta ejecucion.

## 3. Compatibilidad H2 - BLOCKED

La configuracion apunta a `jdbc:h2:file:./data/base_de_datos` y usa `ddl-auto=update`. Se identificaron procesos Java 14848 y 12212 asociados al proyecto; no se ha establecido que la base este cerrada. Se considero inseguro abrirla o copiarla. No se ha verificado esquema real, recuentos, claves, datos, duplicados, nulos ni precios historicos de esa base. Tampoco se ha ejecutado su actualizacion. La prueba no se ha sustituido por una base vacia.

Para completarla: detener correctamente la aplicacion y todas las conexiones, confirmar que H2 esta cerrada, obtener una copia de seguridad coherente y dos copias aisladas, inspeccionar recuentos y relaciones en una, arrancar exclusivamente sobre la otra y comparar datos y esquema. El arranque incluye inicializacion de registros y pedido de demostracion, por lo que cualquier diferencia debe atribuirse al inicio o a la actualizacion antes de concluir que existe perdida de datos. Conservar siempre intactos los archivos originales.

## 4. Auditoria visual - NOT_TESTED

No hay herramientas de navegador automatizado conectadas ni paquetes Playwright, Puppeteer o jsdom disponibles. No se abrieron ni probaron visualmente inicio, Shop, catalogo, detalle, wishlist, valoraciones, carrito, checkout, administracion, idiomas o rebranding en 1440, 768 o 390 px. Quedan sin verificar raton, teclado, desbordamientos, foco, capas, Escape, movimiento reducido, imagen ausente y consola del navegador. Las pruebas MockMvc y la sintaxis JavaScript no sustituyen estas comprobaciones.

## 5. Seguridad

- **Alto - confirmado por inspeccion:** `ControladorAdmin` contiene una clave administrativa fija conocida. No se ha cambiado por instruccion expresa. Impide exponer el proyecto de forma segura en Internet.
- **Alto - confirmado por inspeccion:** `ServicioSetUpImplJPA` puede crear una cuenta de demostracion con credenciales conocidas si se dan las condiciones de inicializacion. No se ha comprobado si existe en la base persistente.
- **Medio - riesgo identificado por inspeccion:** el registro comprueba si existe el correo antes de insertar, pero `Usuario` no declara una restriccion unica. Solicitudes simultaneas podrian crear duplicados; la comprobacion de login exige un unico resultado. No se ha reproducido este escenario ni se ha aplicado una migracion.
- **Medio - limitacion confirmada en codigo:** una linea de pedido antigua sin `precioUnitario` utiliza el precio actual como estimacion. No se puede garantizar su importe historico sin datos adicionales.
- **Medio - configuracion condicionada:** la consola H2 esta desactivada por defecto, pero puede habilitarse mediante variable de entorno; no debe exponerse en un despliegue publico. La politica general de Spring permite peticiones y la proteccion de `/admin` depende del interceptor; se han comprobado rutas representativas mediante tests, no todas las configuraciones de despliegue.
- **Inspeccion y pruebas parciales:** las operaciones de carrito, favoritos y pedidos usan usuario de sesion; las modificaciones inspeccionadas usan POST o DELETE y la suite comprueba CSRF en casos representativos. Las tablas de favoritos y valoraciones declaran unicidad; el voto bloquea al usuario durante la actualizacion. Esto no constituye una auditoria exhaustiva de seguridad.

## 6. Errores corregidos

No se han modificado archivos de aplicacion ni pruebas durante esta auditoria. Se ha creado exclusivamente este informe. No se han aplicado cambios de esquema, commits ni despliegues.

## 7. Errores y comprobaciones pendientes

- Alta: retirar o proteger credenciales administrativas y de demostracion antes de cualquier publicacion. No modificar automaticamente los datos existentes.
- Media: revisar duplicados de correo en una copia y disenar una restriccion unica y migracion segura antes de aplicarla.
- Media: comprobar la compatibilidad de H2 existente en una copia aislada y registrar las diferencias de inicializacion.
- Media: ejecutar la auditoria visual y los recorridos completos en entorno aislado.
- Media: identificar pedidos antiguos sin precio historico y presentar su importe como estimacion; no inventar importes historicos.
- Baja: actualizar fragmentos Thymeleaf obsoletos y revisar advertencias de Mockito y consultas sin tipado cuando se autorice trabajo de mantenimiento.

## 8. Estado de las cinco fases

| Fase | Estado de auditoria | Evidencia y limite |
|---|---|---|
| 1 - modelo y administracion | PARCIAL | Tests de creacion, edicion y validacion superados; H2 real y visual pendientes. |
| 2 - ficha premium | PARCIAL | Endpoint, imagen y precios probados; presentacion y compra en navegador pendientes. |
| 3 - navegacion y wishlist | PARCIAL | Tests de favoritos, sesion, CSRF y menu superados; interaccion y persistencia tras nuevo login en navegador pendientes. |
| 4 - estilo global | PARCIAL | Recursos compilan y pruebas generales pasan; sin verificacion visual, contraste o responsive real. |
| 5 - valoraciones y recomendaciones | PARCIAL | Media, actualizacion, concurrencia, borrados y afinidad basica probados; navegador y H2 real pendientes. |

## 9. Veredicto final

**APTO CON OBSERVACIONES** para entrega academica local si se aceptan expresamente las comprobaciones pendientes de datos y de interfaz. **NO APTO para publicacion en Internet** por las credenciales conocidas y por la ausencia de una validacion de seguridad y despliegue completa. No se afirma que los datos de la base persistente esten migrados o intactos tras la actualizacion, porque esa prueba esta bloqueada.

| Problema pendiente | Prioridad | Archivo | Accion necesaria |
|---|---|---|---|
| Credenciales conocidas de administrador y demostracion | Alta | `ControladorAdmin.java`, `ServicioSetUpImplJPA.java` | Sustituir por configuracion segura antes de publicar, previa decision sobre datos existentes. |
| Compatibilidad de datos H2 persistentes | Media | `application.properties`, `data/base_de_datos.mv.db` | Detener conexiones y verificar migracion en copia coherente aislada. |
| Correo sin unicidad atomica | Media | `Usuario.java`, `ServicioUsuariosImplJPA.java` | Analizar duplicados y preparar migracion segura con restriccion apropiada. |
| Pedidos antiguos sin precio guardado | Media | `ProductoPedido.java` | Identificar registros y comunicar que el importe es estimado. |
| Revision visual e interacciones | Media | `templates/index.html`, `static/css/` | Probar escritorio, tablet y movil en navegador aislado. |
