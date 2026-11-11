/* Galerias del catalogo: una consulta agrupada, sin dependencias adicionales. */
(function () {
    'use strict';
    const cache = new Map(), posiciones = new Map(), pendientes = new Map();
    let versionFavoritos = 0;
    window.obtenerGaleria = function (id) {
        id = Number(id);
        if (cache.has(id)) return $.Deferred().resolve(cache.get(id)).promise();
        const existente = pendientes.get(id);
        const peticion = existente || $.getJSON('/restPerfumes/galerias?ids=' + encodeURIComponent(id));
        if (!existente) {
            pendientes.set(id, peticion);
            peticion.done(galerias => galerias.forEach(g => cache.set(Number(g.id), g.imagenes))).always(() => pendientes.delete(id));
        }
        return peticion.then(galerias => { const entrada = galerias.find(g => Number(g.id) === id); return entrada ? entrada.imagenes : []; });
    };
    const tarjetas = () => document.querySelectorAll('#catalogo_resultados .product-card, #favoritos_resultados .product-card');
    function pintar(card, urls) {
        const id = Number(card.dataset.productoId), img = card.querySelector('.imagen-producto');
        const indice = Math.min(posiciones.get(id) || 0, Math.max(0, urls.length - 1));
        posiciones.set(id, indice);
        card.querySelectorAll('.galeria-anterior, .galeria-siguiente, .galeria-indicador').forEach(el => { el.hidden = urls.length < 2; });
        card.querySelector('.galeria-indicador').textContent = (indice + 1) + ' / ' + urls.length;
        img.hidden = !urls.length;
        card.querySelector('.product-sin-imagen').hidden = !!urls.length;
        if (urls.length && img.getAttribute('src') !== urls[indice]) img.src = urls[indice];
    }
    function actualizar(id, delta) {
        const urls = cache.get(id); if (!urls || urls.length < 2) return;
        posiciones.set(id, ((posiciones.get(id) || 0) + delta + urls.length) % urls.length);
        const card = Array.from(tarjetas()).find(c => Number(c.dataset.productoId) === id);
        if (card) { const img = card.querySelector('.imagen-producto'); img.classList.remove('galeria-aparece'); pintar(card, urls); void img.offsetWidth; img.classList.add('galeria-aparece'); }
    }
    window.marcarFavoritoCatalogo = function (id, activo) {
        document.querySelectorAll('.product-favorito[data-id="' + id + '"]').forEach(b => {
            b.setAttribute('aria-pressed', String(activo));
            b.setAttribute('aria-label', LVLI18n.t(activo ? 'Eliminar de favoritos' : 'Anadir a favoritos'));
            b.querySelector('span').textContent = activo ? '♥' : '♡';
        });
    };
    window.iniciarGaleriasCatalogo = function (ids, version, versionActual) {
        const visibles = new Set(ids.map(Number));
        for (const id of posiciones.keys()) if (!visibles.has(id)) posiciones.delete(id);
        tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
        const existentes = new Set(ids.map(id => pendientes.get(Number(id))).filter(Boolean));
        existentes.forEach(peticion => peticion.done(function () {
            if (version !== versionActual()) return;
            tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
        }));
        const faltantes = ids.filter(id => !cache.has(Number(id)) && !pendientes.has(Number(id)));
        if (faltantes.length) {
            const query = faltantes.map(id => 'ids=' + encodeURIComponent(id)).join('&');
            const peticion = $.getJSON('/restPerfumes/galerias?' + query);
            faltantes.forEach(id => pendientes.set(Number(id), peticion));
            peticion.done(function (galerias) {
                galerias.forEach(g => cache.set(Number(g.id), g.imagenes));
                if (version !== versionActual()) return;
                tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
            }).always(function () { faltantes.forEach(id => pendientes.delete(Number(id))); });
        }
        const favVersion = ++versionFavoritos;
        $.getJSON('/restFavoritos').done(function (productos) {
            if (favVersion !== versionFavoritos || version !== versionActual()) return;
            const favoritos = new Set(productos.map(p => Number(p.id)));
            tarjetas().forEach(card => window.marcarFavoritoCatalogo(Number(card.dataset.productoId), favoritos.has(Number(card.dataset.productoId))));
        });
    };
    window.iniciarGaleriasFavoritos = function (ids, version, versionActual) {
        tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
        const faltantes = ids.filter(id => !cache.has(Number(id)) && !pendientes.has(Number(id)));
        for (let i = 0; i < faltantes.length; i += 50) {
            const lote = faltantes.slice(i, i + 50), query = lote.map(id => 'ids=' + encodeURIComponent(id)).join('&');
            const peticion = $.getJSON('/restPerfumes/galerias?' + query);
            lote.forEach(id => pendientes.set(Number(id), peticion));
            peticion.done(function (galerias) {
                galerias.forEach(g => cache.set(Number(g.id), g.imagenes));
                if (version !== versionActual()) return;
                tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
            }).always(() => lote.forEach(id => pendientes.delete(Number(id))));
        }
        ids.filter(id => pendientes.has(Number(id)) && !faltantes.includes(id)).forEach(id => pendientes.get(Number(id)).done(function () {
            if (version !== versionActual()) return;
            tarjetas().forEach(card => { const urls = cache.get(Number(card.dataset.productoId)); if (urls) pintar(card, urls); });
        }));
    };
    $(document).on('click', '.galeria-anterior, .galeria-siguiente', function () {
        actualizar(Number(this.closest('.product-card').dataset.productoId), this.classList.contains('galeria-siguiente') ? 1 : -1);
    });
    document.addEventListener('error', function (e) {
        const img = e.target;
        if (!img.matches || !img.matches('#catalogo_resultados .imagen-producto, #favoritos_resultados .imagen-producto')) return;
        img.hidden = true; img.closest('.product-media').querySelector('.product-sin-imagen').hidden = false;
    }, true);
    let toque = null;
    $(document).on('touchstart', '#catalogo_resultados .product-media, #favoritos_resultados .product-media', function (e) {
        if (e.originalEvent.touches.length !== 1 || e.target.closest('button')) return;
        const t = e.originalEvent.touches[0]; toque = {x: t.clientX, y: t.clientY, card: this.closest('.product-card')};
    });
    $(document).on('touchend', '#catalogo_resultados .product-media, #favoritos_resultados .product-media', function (e) {
        if (!toque || toque.card !== this.closest('.product-card')) return;
        const t = e.originalEvent.changedTouches[0], dx = t.clientX - toque.x, dy = t.clientY - toque.y;
        if (Math.abs(dx) > 40 && Math.abs(dx) > Math.abs(dy) * 1.3) actualizar(Number(toque.card.dataset.productoId), dx < 0 ? 1 : -1);
        toque = null;
    });
    $(document).on('touchcancel', '#catalogo_resultados .product-media, #favoritos_resultados .product-media', function () { toque = null; });
})();
