# Prompt para Codex — ejecutar y verificar la auditoría final de LVL ESSENCE

Trabaja sobre la versión ACTUAL de LVL ESSENCE que está abierta en este repositorio, con las fases 1–6 instaladas.

**Modo auditoría, sin cambiar código de producción, sin commits ni migraciones de datos.** Conserva `.git`, `data/` y todos los archivos de usuarios. No inicies Spring Boot contra la base de desarrollo hasta haber verificado que existe un respaldo independiente. Las pruebas deben utilizar exclusivamente URLs `jdbc:h2:mem:`.

1. Lee `LEEME_AUDITORIA_FINAL.md` y revisa el nuevo `OfertasFinalTest.java` antes de ejecutarlo. Adáptalo si ha cambiado la API real del proyecto, pero no alteres la lógica de producción ni cambies tests solo para que pasen.
2. Inspecciona todos los tests y verifica que ningún contexto puede usar `jdbc:h2:file:./data/base_de_datos`.
3. Ejecuta **una sola compilación limpia** y toda la suite Maven en el proyecto real. Registra el comando exacto, versión de Java, test total, fallos, errores y omitidos. En caso de error, aporta la causa real con archivo/método y el trozo de log necesario.
4. Revisa los informes `target/surefire-reports`; no confundas métodos JUnit con número de casos o comprobaciones.
5. Valida especialmente: búsqueda/ordenación por precio con descuento, aplicación de ofertas por POST+CSRF, carrito con precio final, precio histórico del pedido tras cambiar la oferta, estados de pedidos y aislamiento de sesiones.
6. Si hay navegador automatizado, prueba Hero + catálogo, menú, páginas informativas, modal ES/EN, checkout, animaciones y preferencia de movimiento reducido; si no lo hay, marca esos casos como pruebas manuales PENDIENTES, no como PASS.
7. Genera `INFORME_AUDITORIA_FINAL_FASES_1_A_6.md` con tabla de PASS/FAIL/BLOCKED/NOT_TESTED, evidencias, riesgos, archivos afectados y veredicto **separado** para entrega académica local y despliegue público.
8. No modifiques datos, contraseñas, migraciones ni reglas de negocio sin mi autorización. Si detectas un fallo, documenta la corrección recomendada antes de aplicarla.

Devuélveme el resumen de pruebas y los fallos exactos si los hubiera.
