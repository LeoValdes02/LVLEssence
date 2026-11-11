(function () {
    'use strict';
    let limpiar = null;
    window.limpiarDetallePremium = function () { if (limpiar) { limpiar(); limpiar = null; } };
    window.marcarFavoritoDetalle = function (id, activo) {
        const boton = document.querySelector('.detalle-favorito[data-id="' + id + '"]');
        if (!boton) return;
        boton.setAttribute('aria-pressed', String(activo));
        boton.querySelector('.favorito-texto').textContent = LVLI18n.t(activo ? 'Eliminar de favoritos' : 'Anadir a favoritos');
        boton.querySelector('.favorito-icono').textContent = activo ? '♥' : '♡';
        boton.dataset.favoritoActivo = String(activo);
    };
    document.addEventListener('lvl:idioma-cambiado', function () {
        const boton = document.querySelector('.detalle-favorito');
        if (boton) window.marcarFavoritoDetalle(Number(boton.dataset.id), boton.dataset.favoritoActivo === 'true');
        document.querySelectorAll('.detalle-miniaturas button').forEach((b, i) => b.setAttribute('aria-label', LVLI18n.t('Mostrar imagen') + ' ' + (i + 1)));
    });
    window.iniciarDetallePremium = function (id, version, versionActual) {
        window.limpiarDetallePremium();
        const raiz = document.querySelector('.detalle-premium'), figura = raiz.querySelector('.detalle-figura');
        const img = raiz.querySelector('.detalle-imagen'), miniaturas = raiz.querySelector('.detalle-miniaturas');
        const ausencia = raiz.querySelector('.detalle-imagen-ausente'), movimiento = raiz.querySelector('.detalle-movimiento');
        const controller = new AbortController(), signal = controller.signal;
        const vigente = () => raiz.isConnected && version === versionActual();
        let urls = [], indice = 0, toque = null, cuadro = 0, x = 0, y = 0;
        function seleccionar(n) {
            if (!urls.length) return;
            indice = (n + urls.length) % urls.length;
            img.hidden = false; ausencia.hidden = true;
            img.classList.remove('detalle-fade'); img.src = urls[indice]; void img.offsetWidth; img.classList.add('detalle-fade');
            miniaturas.querySelectorAll('button').forEach((b, i) => b.setAttribute('aria-pressed', String(i === indice)));
        }
        window.obtenerGaleria(id).done(function (imagenes) {
            if (!vigente()) return;
            urls = imagenes;
            if (!urls.length) { img.hidden = true; ausencia.hidden = false; return; }
            if (urls.length > 1) {
                miniaturas.hidden = false;
                urls.forEach(function (url, i) {
                    const b = document.createElement('button'), thumb = document.createElement('img');
                    b.type = 'button'; b.setAttribute('aria-label', LVLI18n.t('Mostrar imagen') + ' ' + (i + 1));
                    b.setAttribute('aria-pressed', String(i === 0)); thumb.src = url; thumb.alt = ''; thumb.loading = 'lazy'; b.append(thumb);
                    b.addEventListener('click', () => seleccionar(i), {signal}); miniaturas.append(b);
                });
            }
            seleccionar(0);
        });
        $.getJSON('/restFavoritos').done(function (productos) {
            if (!vigente()) return;
            window.marcarFavoritoDetalle(id, productos.some(p => Number(p.id) === id));
            raiz.querySelector('.detalle-favorito-estado').textContent = '';
            raiz.querySelector('.detalle-favorito').disabled = false;
        }).fail(function (xhr) {
            if (!vigente()) return;
            raiz.querySelector('.detalle-favorito-estado').textContent = xhr.status === 401 ? '' : LVLI18n.t('No se pudieron cargar los favoritos.');
            if (xhr.status === 401) raiz.querySelector('.detalle-favorito').disabled = false;
        });
        raiz.querySelectorAll('.detalle-menos, .detalle-mas').forEach(b => b.addEventListener('click', function () {
            const campo = raiz.querySelector('.detalle-cantidad');
            if (!campo.value || !Number.isFinite(Number(campo.value))) campo.value = '1';
            if (b.classList.contains('detalle-menos')) campo.stepDown(); else campo.stepUp();
            campo.dispatchEvent(new Event('input', {bubbles: true}));
        }, {signal}));
        figura.addEventListener('touchstart', e => { if (e.touches.length === 1) toque = {x:e.touches[0].clientX, y:e.touches[0].clientY}; }, {signal, passive:true});
        figura.addEventListener('touchend', e => { if (!toque) return; const t = e.changedTouches[0], dx = t.clientX - toque.x, dy = t.clientY - toque.y; if (urls.length > 1 && Math.abs(dx) > 40 && Math.abs(dx) > Math.abs(dy) * 1.3) seleccionar(indice + (dx < 0 ? 1 : -1)); toque = null; }, {signal, passive:true});
        figura.addEventListener('touchcancel', () => { toque = null; }, {signal});
        const reducido = matchMedia('(prefers-reduced-motion: reduce)'), puntero = matchMedia('(hover: hover) and (pointer: fine)');
        function aplicar() { cuadro = 0; if (!vigente() || reducido.matches) return; const r = figura.getBoundingClientRect(); const profundidad = Math.max(-8, Math.min(8, (innerHeight / 2 - (r.top + r.height / 2)) * .015)); movimiento.style.transform = 'perspective(900px) translateY(' + profundidad + 'px) rotateX(' + y + 'deg) rotateY(' + x + 'deg)'; }
        function solicitar() { if (!cuadro && !reducido.matches && puntero.matches) cuadro = requestAnimationFrame(aplicar); }
        figura.addEventListener('pointermove', e => { if (e.pointerType !== 'mouse') return; const r = figura.getBoundingClientRect(); x = ((e.clientX-r.left)/r.width-.5)*8; y = -((e.clientY-r.top)/r.height-.5)*8; solicitar(); }, {signal});
        figura.addEventListener('pointerleave', () => { x = y = 0; solicitar(); }, {signal});
        window.addEventListener('scroll', solicitar, {signal, passive:true});
        reducido.addEventListener('change', () => { if (reducido.matches) { if (cuadro) cancelAnimationFrame(cuadro); cuadro = 0; movimiento.style.transform = ''; } }, {signal});
        limpiar = () => { controller.abort(); if (cuadro) cancelAnimationFrame(cuadro); movimiento.style.transform = ''; };
    };
})();
