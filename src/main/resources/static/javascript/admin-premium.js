(function () {
    'use strict';
    const menu = document.getElementById('admin-menu');
    const abrir = document.querySelector('.admin-menu-abrir');
    if (!menu || !abrir) return;
    const cerrar = menu.querySelector('.admin-menu-cerrar');
    const movil = window.matchMedia('(max-width: 1050px)');
    const prefijo = 'lvl-admin-ofertas:';
    function estado(abierto, devolverFoco) {
        menu.hidden = movil.matches && !abierto;
        abrir.setAttribute('aria-expanded', String(movil.matches && abierto));
        if (devolverFoco && movil.matches) abrir.focus();
    }
    abrir.addEventListener('click', function () {
        const abierto = abrir.getAttribute('aria-expanded') !== 'true';
        estado(abierto, !abierto);
        if (abierto) cerrar.focus();
    });
    cerrar.addEventListener('click', () => estado(false, true));
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && movil.matches && !menu.hidden) { estado(false, true); e.preventDefault(); }
    });
    menu.querySelectorAll('a').forEach(a => a.addEventListener('click', () => estado(false, false)));
    movil.addEventListener('change', () => estado(false, false));
    document.documentElement.classList.add('admin-menu-js');
    estado(false, false);
    const ruta = location.pathname.toLowerCase();
    const secciones = [['perfume', 'gestionarPerfumes'], ['usuario', 'gestionarUsuarios'], ['categor', 'gestionarCategorias'], ['pedido', 'gestionarPedidos']];
    const activa = secciones.find(([clave]) => ruta.includes(clave));
    if (activa) {
        const enlace = Array.from(menu.querySelectorAll('a')).find(a => a.getAttribute('href').includes(activa[1]));
        if (enlace) enlace.setAttribute('aria-current', 'page');
    }
    const seleccion = Array.from(document.querySelectorAll('input[name="ids"][form="admin-descuentos-lote"]'));
    const clave = prefijo + location.pathname + location.search;
    try {
        const guardados = JSON.parse(sessionStorage.getItem(clave) || '[]');
        if (Array.isArray(guardados)) seleccion.forEach(input => { input.checked = guardados.includes(input.value); });
    } catch (_) { /* almacenamiento opcional */ }
    seleccion.forEach(input => input.addEventListener('change', function () {
        try { sessionStorage.setItem(clave, JSON.stringify(seleccion.filter(c => c.checked).map(c => c.value))); } catch (_) { /* no disponible */ }
    }));
    const logout = document.querySelector('.admin-logout');
    if (logout) logout.addEventListener('submit', function () {
        try { for (let i = sessionStorage.length - 1; i >= 0; i--) { const k = sessionStorage.key(i); if (k && k.startsWith(prefijo)) sessionStorage.removeItem(k); } } catch (_) { /* no disponible */ }
    });
})();
