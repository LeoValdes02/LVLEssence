# LVL ESSENCE — Fase 4: inicio, navegación y páginas informativas

## Punto de partida

Este paquete está preparado sobre el proyecto `LVLEssence.zip` **más las fases 1.1, 1.2, 1.3, 2 y 3**, en ese orden. No instala dichas fases ni reemplaza código Java. Si la fase 3 todavía no está instalada, instálala primero y haz una copia de seguridad de tu proyecto.

## Incluye

- `/` y `/#inicio`: Hero visible al cargar la tienda y catálogo filtrable inmediatamente debajo.
- `/#perfumes`: catálogo completo con filtros, ordenación y paginación de la fase 1.
- Menú principal solo con: Perfumes, Iniciar sesión, Registrarme, Contacto, Sobre nosotros.
- Icono de carrito independiente. Icono de cuenta para usuarios identificados, con acceso a Cerrar sesión.
- Páginas informativas navegables por hash (`/#contacto`, `/#sobre-nosotros`, `/#privacidad`, `/#devoluciones`, `/#terminos`). Enlaces también en el pie.
- Estilos responsive para el nuevo menú, el pie y las páginas.

## Limitaciones reconocidas (importantes)

1. Las páginas **Contacto, Privacidad, Devoluciones y Términos son borradores**. No inventamos dirección, correo, condiciones de venta ni plazos legales. Antes de publicar hay que incorporar datos reales, textos legales revisados y, si se desea recibir mensajes, un formulario con backend o un correo verificado.
2. `Mis pedidos` y `Mis datos` ya figuraban en el menú original, pero **no tienen funcionalidad conectada en el `index.html` recibido**. Se han trasladado al menú de cuenta y ahora explican explícitamente que las vistas están pendientes; NO se ha inventado un endpoint de usuario.
3. No se ha modificado el login, CSRF, JPA, carrito, checkout, oferta, estados de pedidos ni H2.
4. Los enlaces usan rutas hash internas a la aplicación, no nuevas rutas MVC independientes.
5. No se ha ejecutado la suite Maven, tal como acordamos. Se ha comprobado la sintaxis JavaScript con Node y el marcado principal.

## Archivos a sobrescribir o añadir

- `src/main/resources/templates/index.html` **sobrescribir**
- `src/main/resources/static/css/estilos.css` **sobrescribir**
- `src/main/resources/static/plantillas_html/inicio.html` **sobrescribir**
- `src/main/resources/static/plantillas_html/contacto.html` **nuevo**
- `src/main/resources/static/plantillas_html/sobre_nosotros.html` **nuevo**
- `src/main/resources/static/plantillas_html/privacidad.html` **nuevo**
- `src/main/resources/static/plantillas_html/devoluciones.html` **nuevo**
- `src/main/resources/static/plantillas_html/terminos.html` **nuevo**

## Instalación

1. Detén Spring Boot.
2. Haz una copia de seguridad del proyecto. No hay cambios de base de datos en esta fase.
3. Comprueba que la fase 3 está ya instalada.
4. Extrae este ZIP sobre la raíz del proyecto, junto a `pom.xml`, respetando rutas `src/...`.
5. Reinicia Spring Boot y abre `http://localhost:8080/`.

## Comprobación visual rápida (sin suite de tests por ahora)

- `/`: aparece Hero y **justo debajo** el catálogo filtrable con tarjetas.
- Pulsa «Explorar perfumes» y comprueba que hace scroll al catálogo.
- En «Perfumes» se ve el catálogo sin Hero; los filtros existentes y ofertas siguen visibles.
- Las cinco opciones principales y los enlaces legales abren el contenido correcto; también al pegar directamente `/#contacto`.
- Registro e inicio de sesión siguen mostrando sus formularios. Inicia sesión y verifica que aparece el icono de cuenta; cierra sesión.
- El carrito sigue accesible mediante el icono y se puede añadir un perfume.
- Comprobar responsive reduciendo el ancho del navegador; comprobar que footer y menú no se solapan.

Los tests de regresión completos quedan reservados para la auditoría final, según lo acordado.
