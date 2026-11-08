# LVL ESSENCE — Auditoría final de las fases 1–6

Fecha: 7 de noviembre de 2026.

## Qué es este paquete

**Una ampliación de pruebas, no una actualización funcional.** Incluye un único archivo Java nuevo con cuatro pruebas de descuentos y un procedimiento de verificación manual. No contiene entidades, controladores, configuración, archivos de base de datos ni cambios en la aplicación en producción.

El código examinado se ha **reconstruido** aplicando en orden los paquetes publicados en esta conversación a `LVLEssence.zip` (versión anterior a esas fases). No se ha recibido todavía un ZIP de tu carpeta local *después* de instalar todas las fases; por ello, cualquier cambio manual hecho en IntelliJ desde entonces puede no estar reflejado.

## Inspección ya realizada

- Los archivos y rutas de búsqueda pública, paginación administrativa, ofertas, cambio de estado, inicio, idioma y animaciones están presentes en la reconstrucción.
- `index.html` carga `i18n.js` y `animaciones.js`; no hay IDs duplicados dentro del propio `index.html`.
- La sintaxis JavaScript de `index.html`, `i18n.js` y `animaciones.js` pasa `node --check`.
- `PrecioPerfume`, `EstadosPedido`, `PerfumeResumen` y `PaginaPerfumes` compilan con Java 17 de manera aislada.
- Dos cálculos (`95 € - 20 % = 76,00 €` y `10,05 € - 50 % = 5,03 €`) y la prohibición de transición administrativa desde `EnProceso` pasan un pequeño programa Java local.
- Se encuentran **18 métodos `@Test` en 8 clases existentes** (contando `EstadosPedidosAdminTest`, no ejecutado todavía según lo comunicado). Sus clases tienen URL explícita `jdbc:h2:mem:`.

**Esto NO equivale a compilar la aplicación entera, ejecutar Maven ni probar el navegador.**

## Instalación del test añadido

1. Detén Spring Boot. Haz copia de seguridad del proyecto y, si tienes que arrancar sobre datos antiguos, también de `data/` con H2 cerrado.
2. Copia `src/test/java/com/leo/tienda/perfumes/OfertasFinalTest.java` a la misma ruta en tu proyecto real.
3. No sobrescribas ningún archivo de `src/main/`.
4. Comprueba que los tests de tu proyecto real utilizan H2 temporal en memoria.
5. Desde la raíz de `LVLEssence`, ejecuta **una sola vez** la suite completa:

   - Windows: `mvnw.cmd clean test`
   - macOS/Linux: `./mvnw clean test`
   - O `mvn clean test` si Maven está instalado.

   Si el wrapper necesita descargar dependencias y no tiene acceso, usa el Maven que ya utilizaste en el proyecto.

6. Revisa `target/surefire-reports/` y comparte el resumen, especialmente los fallos de `OfertasFinalTest`.

La prueba nueva usa `jdbc:h2:mem:ofertas_final;DB_CLOSE_DELAY=-1`, no tu base `jdbc:h2:file:./data/base_de_datos`.

## Cobertura del test añadido

| Test | Qué comprueba |
| --- | --- |
| `calculoMonetarioYLimitesDeDescuento` | Redondeo de precio final, 0 %, 100 % y rechazo de porcentajes imposibles. |
| `busquedaYOrdenacionUtilizanPrecioConDescuento` | Que la búsqueda por precio y la ordenación usan 70 € tras descuento, no 100 € originales. |
| `pedidoConservaImporteDespuesDeCambiarOferta` | Que el carrito aplica la oferta y que el pedido conserva el precio unitario al quitarla después. |
| `descuentoAdministrativoSoloPostConSesionYCsrf` | Que la operación exige POST, sesión administrativa y token CSRF. |

Son tests nuevos preparados y **aún no ejecutados**. Si superan los 18 existentes, la suite pasaría a tener **22 métodos JUnit**; ese número es una previsión, no un resultado verificado.

## Comprobación final en navegador (manual)

### 1. Catálogo
- Inicio: Hero y catálogo debajo; menú «Perfumes» abre solo catálogo.
- Buscar un nombre, combinar categoría y precio; ordenar por precio; cambiar de página.
- Crear una oferta desde administración: el precio mostrado, el filtrado y la ordenación deben utilizar el precio **rebajado**.
- Quitar la oferta (0 %) y comprobar recuperación del precio anterior.

### 2. Pedidos
- Confirmar una compra con el perfume en oferta; comprobar carrito vacío y pedido `Completado`.
- Cambiar el porcentaje después y verificar que el pedido antiguo **no cambia de total**.
- Desde administración cambiar `Completado` → `Enviado` → `Incidencia`.
- Comprobar que no se permite cambiar `EnProceso` manualmente.

### 3. Seguridad
- Probar sin sesión una ruta `/admin/**`; debe redirigir al login.
- Probar la antigua URL `?pass-login-admin=123` en ventana privada; no debe conceder permisos.
- Comprobar que los POST protegidos sin token CSRF devuelven 403.
- Verificar login y logout de cliente, y que el carrito de otra cuenta no sea accesible.

### 4. Navegación e idiomas
- Comprobar los cinco enlaces principales, las páginas informativas y el icono de carrito.
- Cambiar ES↔EN durante un formulario, actualizar y comprobar persistencia sin perder datos.
- Comprobar que nombres de perfumes y precios **no se traducen accidentalmente**.
- Recordar que «Mis pedidos» y «Mis datos» aún pueden estar pendientes de conectar; páginas legales son borradores.

### 5. Animaciones y accesibilidad
- Abrir portada, hacer scroll y aplicar filtros; las tarjetas nuevas deben animarse sin desaparecer.
- Activar en el sistema «Reducir movimiento» y verificar que la tienda sigue siendo visible.
- Probar con teclado y en ventana móvil; comprobar menú, modal e interacciones.

### 6. Archivos e imágenes
- Crear y editar un perfume con imagen desde administración: comprobar envío multipart y CSRF.
- Confirmar que la foto sigue disponible y que borrar un perfume asociado a un pedido se impide con un mensaje legible.

## Riesgos conocidos que no debemos ocultar

- No se ha ejecutado el conjunto Spring Boot completo aquí porque Maven no está disponible en el entorno de preparación.
- La base H2 real y los posibles datos antiguos no han sido probados contra esta reconstrucción.
- La actualización periódica de 60 segundos de catálogo, ficha y carrito podría provocar cambios visuales mientras el cliente interactúa; comprobarlo en navegador.
- El resumen de checkout no advierte de una oferta que cambie entre el resumen y la confirmación: debe revisarse antes de pagos reales.
- La contraseña de administrador `123` sigue siendo insegura fuera del entorno local de aprendizaje.
- Los textos de privacidad, devoluciones y condiciones aún requieren datos reales y revisión antes de publicar.

## Criterio de cierre

Solo dar las seis fases por **verificadas** cuando:

1. `mvn clean test` termine con `BUILD SUCCESS` y cero errores/omisiones inesperados.
2. El resultado confirme conexiones `jdbc:h2:mem:` en todas las clases de prueba.
3. Se completen los flujos manuales anteriores.
4. No haya errores 403 inesperados, excepciones de Hibernate ni cambios en el precio histórico.
5. Se documenten los límites conocidos y las funcionalidades no terminadas.
