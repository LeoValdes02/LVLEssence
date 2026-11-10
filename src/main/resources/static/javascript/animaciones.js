/* LVL ESSENCE · Fase 6: animaciones progresivas de la tienda publica.
 * - IntersectionObserver: solo anima cuando un elemento entra en pantalla.
 * - MutationObserver: detecta nuevas tarjetas creadas por AJAX/Mustache.
 * - No modifica el HTML de los formularios, los enlaces ni las APIs.
 * - Sin JavaScript, sin IntersectionObserver o con movimiento reducido,
 *   toda la interfaz sigue siendo visible y utilizable.
 */
(function () {
    "use strict";

    function iniciarAnimaciones() {
        const raiz = document.getElementById("contenedor");
        if (!raiz) return;

        const movimientoReducido = window.matchMedia &&
            window.matchMedia("(prefers-reduced-motion: reduce)");

        if (!window.IntersectionObserver || !window.MutationObserver ||
            (movimientoReducido && movimientoReducido.matches)) return;

        // Esta clase habilita las transiciones CSS SOLO si hay soporte JS.
        document.documentElement.classList.add("lvl-motion-enabled");

        const elementosAnimables = [
            ".hero-content",
            ".catalogo-encabezado",
            ".catalogo-filtros",
            ".product-card",
            ".pagina-informativa",
            ".detalle-nota"
        ].join(", ");

        const pendientes = new Set();
        function revelar(elemento) {
            elemento.classList.add("lvl-visible");
            pendientes.delete(elemento);
            entrada.unobserve(elemento);
        }
        const entrada = new IntersectionObserver(function (entradas) {
            for (const elemento of entradas) {
                if (!elemento.isIntersecting) continue;
                revelar(elemento.target);
            }
        }, {
            threshold: 0.08,
            rootMargin: "0px 0px -24px 0px"
        });

        function registrarElementos() {
            const visibles = raiz.querySelectorAll(elementosAnimables);
            let indiceTarjeta = 0;
            for (const elemento of visibles) {
                if (elemento.dataset.lvlAnimado === "1") continue;
                elemento.dataset.lvlAnimado = "1";
                elemento.classList.add("lvl-entrada");

                // Escalonado corto para no retrasar una pagina larga.
                if (elemento.matches(".product-card")) {
                    elemento.style.setProperty(
                        "--lvl-delay", Math.min(indiceTarjeta % 10, 5) * 55 + "ms"
                    );
                    indiceTarjeta++;
                }

                pendientes.add(elemento);
                entrada.observe(elemento);
            }
        }

        function liberarElementosEliminados() {
            for (const elemento of pendientes) {
                if (!elemento.isConnected) {
                    entrada.unobserve(elemento);
                    pendientes.delete(elemento);
                }
            }
        }

        let actualizacionProgramada = false;
        const cambios = new MutationObserver(function (mutaciones) {
            // Ignoramos cambios de texto y atributos: i18n tiene su propio observador.
            if (!mutaciones.some(function (m) { return m.addedNodes.length || m.removedNodes.length; })) return;
            if (actualizacionProgramada) return;
            actualizacionProgramada = true;
            window.requestAnimationFrame(function () {
                actualizacionProgramada = false;
                liberarElementosEliminados();
                registrarElementos();
            });
        });

        cambios.observe(raiz, {childList: true, subtree: true});
        registrarElementos();
        raiz.addEventListener("focusin", function (evento) {
            const elemento = evento.target.closest(elementosAnimables);
            if (elemento && pendientes.has(elemento)) revelar(elemento);
        });

        // Si la preferencia del sistema cambia mientras la pagina esta abierta,
        // el usuario debe poder ver inmediatamente cualquier contenido pendiente.
        if (movimientoReducido) {
            const actualizarPreferencia = function (evento) {
                if (!evento.matches) return;
                document.documentElement.classList.remove("lvl-motion-enabled");
                for (const elemento of pendientes) {
                    elemento.classList.add("lvl-visible");
                    entrada.unobserve(elemento);
                }
                pendientes.clear();
                entrada.disconnect();
                cambios.disconnect();
            };
            if (movimientoReducido.addEventListener) {
                movimientoReducido.addEventListener("change", actualizarPreferencia);
            } else if (movimientoReducido.addListener) {
                movimientoReducido.addListener(actualizarPreferencia);
            }
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", iniciarAnimaciones, {once: true});
    } else {
        iniciarAnimaciones();
    }
})();
