/* Navegacion publica, favoritos y menu; datos siempre procedentes del servidor. */
(function () {
    'use strict';
    const mobile = document.getElementById('nav-movil');
    const nav = document.getElementById('nav-enlaces');
    const header = document.getElementById('navegacion-premium');
    const search = document.getElementById('panel-busqueda');
    const searchButton = document.getElementById('abrir-busqueda');
    const searchMobile = document.getElementById('buscar-movil');
    const account = document.getElementById('menu_usuario');
    const campoBusqueda = document.getElementById('buscar-nav');
    const panelSugerencias = document.getElementById('sugerencias-panel');
    const listaSugerencias = document.getElementById('sugerencias-lista');
    const estadoSugerencias = document.getElementById('sugerencias-estado');
    let favoritoPendiente = null, versionCarrito = 0, searchTrigger = searchButton;
    let esperaSugerencias, controladorSugerencias, versionSugerencias = 0, opcionesSugerencias = [], indiceSugerencia = -1, composicion = false, ultimaConsulta = '', ultimoResultado = null;
    function cancelarSugerencias() {
        clearTimeout(esperaSugerencias); versionSugerencias++;
        if (controladorSugerencias) controladorSugerencias.abort();
        controladorSugerencias = null;
    }
    function cerrarSugerencias() {
        cancelarSugerencias(); panelSugerencias.hidden = true; campoBusqueda.setAttribute('aria-expanded', 'false');
        campoBusqueda.removeAttribute('aria-activedescendant'); listaSugerencias.replaceChildren(); estadoSugerencias.textContent = '';
        opcionesSugerencias = []; indiceSugerencia = -1;
    }
    function mostrarPanel() { panelSugerencias.hidden = false; campoBusqueda.setAttribute('aria-expanded', 'true'); }
    function actualizarOpcion() {
        const opciones = listaSugerencias.querySelectorAll('[role="option"]');
        opciones.forEach((opcion, i) => opcion.setAttribute('aria-selected', String(i === indiceSugerencia)));
        if (indiceSugerencia >= 0) { const opcion = opciones[indiceSugerencia]; campoBusqueda.setAttribute('aria-activedescendant', opcion.id); opcion.scrollIntoView({block:'nearest'}); }
        else campoBusqueda.removeAttribute('aria-activedescendant');
    }
    function pintarSugerencias(productos) {
        listaSugerencias.replaceChildren(); campoBusqueda.removeAttribute('aria-activedescendant'); indiceSugerencia = -1;
        opcionesSugerencias = productos.slice(0, 6).filter(p => Number.isSafeInteger(Number(p.id)) && Number(p.id) > 0);
        estadoSugerencias.textContent = opcionesSugerencias.length ? '' : 'Sin resultados.';
        opcionesSugerencias.forEach(function (p, i) {
            const li = document.createElement('li'); li.id = 'sugerencia-' + i; li.setAttribute('role', 'option'); li.setAttribute('aria-selected', 'false'); li.dataset.indice = String(i);
            const imagen = document.createElement('img'); imagen.src = '/mostrar_imagen?id=' + encodeURIComponent(p.id); imagen.alt = ''; imagen.width = 48; imagen.height = 48; imagen.loading = 'lazy';
            const datos = document.createElement('span'); datos.className = 'sugerencia-datos'; datos.dataset.noTraducir = '';
            const marca = document.createElement('span'); marca.textContent = p.marca || '';
            const nombre = document.createElement('strong'); nombre.textContent = p.nombre || '';
            const precio = document.createElement('span'); precio.textContent = Number(p.precio).toFixed(2).replace('.', ',') + ' EUR';
            datos.append(marca, nombre, precio); li.append(imagen, datos); listaSugerencias.append(li);
        });
        mostrarPanel();
    }
    function buscarSugerencias() {
        cancelarSugerencias();
        const texto = campoBusqueda.value.trim();
        if (composicion || Array.from(texto).length < 2) { cerrarSugerencias(); return; }
        mostrarPanel(); listaSugerencias.replaceChildren(); opcionesSugerencias = []; indiceSugerencia = -1; campoBusqueda.removeAttribute('aria-activedescendant');
        if (texto === ultimaConsulta && ultimoResultado) { pintarSugerencias(ultimoResultado); return; }
        estadoSugerencias.textContent = 'Buscando...';
        const version = versionSugerencias;
        esperaSugerencias = setTimeout(async function () {
            const controlador = new AbortController(); controladorSugerencias = controlador;
            try {
                const parametros = new URLSearchParams({texto: texto, pagina: '1', orden: 'nombreAsc'});
                const respuesta = await fetch('/restPerfumes/buscar?' + parametros, {signal: controlador.signal});
                if (!respuesta.ok) throw new Error('search');
                const datos = await respuesta.json();
                if (version !== versionSugerencias || search.hidden || campoBusqueda.value.trim() !== texto) return;
                if (!Array.isArray(datos.perfumes)) throw new Error('response');
                ultimaConsulta = texto; ultimoResultado = datos.perfumes; pintarSugerencias(datos.perfumes);
            } catch (error) {
                if (error.name === 'AbortError' || version !== versionSugerencias) return;
                estadoSugerencias.textContent = 'Error al buscar. Puedes realizar la búsqueda completa.';
            } finally { if (controladorSugerencias === controlador) controladorSugerencias = null; }
        }, 250);
    }
    function seleccionarSugerencia(indice) {
        const producto = opcionesSugerencias[indice]; if (!producto) return;
        cerrarMenu(); document.getElementById('contenido-principal').focus({preventScroll:true}); mostrar_detalles(Number(producto.id));
    }
    campoBusqueda.addEventListener('compositionstart', function () { composicion = true; cerrarSugerencias(); });
    campoBusqueda.addEventListener('compositionend', function () { composicion = false; buscarSugerencias(); });
    campoBusqueda.addEventListener('input', function (e) { if (!composicion && !e.isComposing) buscarSugerencias(); });
    campoBusqueda.addEventListener('focus', function () { if (panelSugerencias.hidden && Array.from(campoBusqueda.value.trim()).length >= 2) buscarSugerencias(); });
    campoBusqueda.addEventListener('keydown', function (e) {
        if (composicion || e.isComposing) return;
        if (e.key === 'Escape' && !panelSugerencias.hidden) { e.preventDefault(); cerrarSugerencias(); return; }
        if (panelSugerencias.hidden && (e.key === 'ArrowDown' || e.key === 'ArrowUp') && Array.from(campoBusqueda.value.trim()).length >= 2) { e.preventDefault(); buscarSugerencias(); return; }
        if (panelSugerencias.hidden || !opcionesSugerencias.length) return;
        if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
            e.preventDefault(); const paso = e.key === 'ArrowDown' ? 1 : -1;
            indiceSugerencia = indiceSugerencia < 0 ? (paso > 0 ? 0 : opcionesSugerencias.length - 1) : (indiceSugerencia + paso + opcionesSugerencias.length) % opcionesSugerencias.length; actualizarOpcion();
        } else if (e.key === 'Enter' && indiceSugerencia >= 0) { e.preventDefault(); seleccionarSugerencia(indiceSugerencia); }
    });
    listaSugerencias.addEventListener('mousedown', function (e) { if (e.target.closest('[role="option"]')) e.preventDefault(); });
    listaSugerencias.addEventListener('click', function (e) { const opcion = e.target.closest('[role="option"]'); if (opcion) seleccionarSugerencia(Number(opcion.dataset.indice)); });
    document.querySelectorAll('.premium-cuenta, .premium-cuenta-movil').forEach(function (detalle) {
        detalle.addEventListener('toggle', function () { detalle.querySelector('summary').setAttribute('aria-expanded', String(detalle.open)); });
    });
    const tarjeta = '<article class="mega-producto"><button type="button" class="mega-detalle" data-id="{{id}}"><img src="/mostrar_imagen?id={{id}}" alt="{{nombre}}"><span data-no-traducir>{{marca}}</span><strong data-no-traducir>{{nombre}}</strong></button>{{#enOferta}}<s>{{precioOriginalTexto}} EUR</s> <span class="mega-descuento">-{{descuentoPorcentaje}}%</span>{{/enOferta}}<b>{{precioTexto}} EUR</b></article>';
    function datosProducto(p) {
        return Object.assign({}, p, {precioTexto: Number(p.precio).toFixed(2).replace('.', ','), precioOriginalTexto: Number(p.precioOriginal).toFixed(2).replace('.', ','), enOferta: Number(p.descuentoPorcentaje) > 0});
    }
    function cerrarBusqueda(enfocar = false) {
        cerrarSugerencias(); ultimaConsulta = ''; ultimoResultado = null;
        search.hidden = true; searchButton.setAttribute('aria-expanded', 'false'); searchMobile.setAttribute('aria-expanded', 'false');
        if (enfocar && searchTrigger.offsetParent !== null) searchTrigger.focus();
    }
    function cerrarMenu(enfocar = false) {
        cerrarBusqueda(); nav.classList.remove('premium-abierto'); mobile.setAttribute('aria-expanded', 'false');
        document.querySelector('.premium-cuenta-movil').open = false;
        if (enfocar && mobile.offsetParent !== null) mobile.focus();
    }
    function abrirBusqueda(trigger) {
        if (!search.hidden) { cerrarBusqueda(true); return; }
        searchTrigger = trigger; search.hidden = false; trigger.setAttribute('aria-expanded', 'true'); account.open = false;
        document.getElementById('buscar-nav').focus();
    }
    mobile.addEventListener('click', function () {
        if (mobile.getAttribute('aria-expanded') === 'true') { cerrarMenu(true); return; }
        mobile.setAttribute('aria-expanded', 'true'); nav.classList.add('premium-abierto');
        document.getElementById('cerrar-menu').focus();
    });
    document.getElementById('cerrar-menu').addEventListener('click', function () { cerrarMenu(true); });
    searchButton.addEventListener('click', function () { abrirBusqueda(searchButton); });
    searchMobile.addEventListener('click', function () { abrirBusqueda(searchMobile); });
    document.getElementById('cerrar-busqueda').addEventListener('click', function () { cerrarBusqueda(true); });
    document.getElementById('idioma-movil').addEventListener('click', function () {
        cerrarMenu();
        document.getElementById('modal_idioma').addEventListener('close', function () { mobile.focus(); }, {once: true});
        document.getElementById('abrir_modal_idioma').click();
    });
    document.querySelector('.cerrar-sesion-movil').addEventListener('click', function (e) { e.preventDefault(); cerrarMenu(); document.getElementById('cerrar_sesion').click(); });
    document.addEventListener('keydown', function (e) {
        if (e.key !== 'Escape' || e.defaultPrevented || e.isComposing) return;
        if (!panelSugerencias.hidden) { cerrarSugerencias(); e.preventDefault(); return; }
        if (!search.hidden) { cerrarBusqueda(true); e.preventDefault(); }
        else if (account.open) { account.open = false; account.querySelector('summary').focus(); e.preventDefault(); }
        else if (mobile.getAttribute('aria-expanded') === 'true') { cerrarMenu(true); e.preventDefault(); }
    });
    document.addEventListener('click', function (e) { if (!header.contains(e.target)) { cerrarMenu(); account.open = false; } });
    document.addEventListener('click', function (e) { if (!search.hidden && !search.contains(e.target) && e.target !== searchButton && !searchButton.contains(e.target) && e.target !== searchMobile && !searchMobile.contains(e.target)) cerrarSugerencias(); });
    search.addEventListener('submit', function (e) {
        e.preventDefault(); filtrosCatalogo.texto = document.getElementById('buscar-nav').value.trim(); paginaCatalogoActual = 1;
        cerrarMenu(); document.getElementById('contenido-principal').focus({preventScroll: true}); irARuta('perfumes');
    });
    $(document).on('click', '.mega-detalle', function () { mostrar_detalles(Number($(this).data('id'))); });
    $(document).on('click', '.premium-enlaces a[data-ruta], .premium-logo, .premium-utilidades a[data-ruta], .premium-carrito-movil', function () {
        const movilAbierto = mobile.getAttribute('aria-expanded') === 'true';
        cerrarMenu(); account.open = false;
        if (movilAbierto) document.getElementById('contenido-principal').focus({preventScroll: true});
    });
    let scrollPendiente = false;
    function actualizarCabecera() {
        if (window.scrollY > 120) header.classList.add('premium-compacta');
        else if (window.scrollY < 60) header.classList.remove('premium-compacta');
        scrollPendiente = false;
    }
    window.addEventListener('scroll', function () { if (!scrollPendiente) { scrollPendiente = true; window.requestAnimationFrame(actualizarCabecera); } }, {passive:true});
    actualizarCabecera();
    let ultimoResumen = null;
    function pintarResumen(r) {
        ultimoResumen = r;
        const texto = r.votos ? Number(r.media).toFixed(1) + ' / 5 (' + r.votos + ' ' + (LVLI18n.idioma() === 'en' ? 'votes' : 'votos') + ')' : LVLI18n.t('Sin valoraciones');
        $('#resumen-valoracion').text(texto);
        if (r.miPuntuacion) $('#puntuacion').val(String(r.miPuntuacion));
    }
    document.addEventListener('lvl:idioma-cambiado', function () { if (ultimoResumen && document.getElementById('resumen-valoracion')) pintarResumen(ultimoResumen); });
    window.cargarValoracion = function (id) {
        ultimoResumen = null;
        const vista = document.querySelector('.detalle-valoracion');
        $.getJSON('/restValoraciones', {id:id}).done(function (r) { if (vista && vista.isConnected) pintarResumen(r); })
            .fail(function () { if (vista && vista.isConnected) $('#resumen-valoracion').text(LVLI18n.t('No se pudieron cargar las valoraciones.')); });
    };
    $(document).on('submit', '#form-valoracion', function (e) {
        e.preventDefault(); const vista = this.closest('.detalle-valoracion'), id = Number(vista.dataset.id);
        $.post('/restValoraciones', {id:id, puntuacion:$('#puntuacion').val()}).done(function (r) {
            if (vista.isConnected) { pintarResumen(r); $('#mensaje-valoracion').text(LVLI18n.t('Puntuacion guardada.')); }
        }).fail(function (xhr) { if (vista.isConnected) $('#mensaje-valoracion').text(LVLI18n.t(xhr.status === 401 ? 'Inicia sesion para valorar.' : 'No se pudo guardar la puntuacion.')); });
    });
    window.actualizarContadorCarrito = function () {
        const version = ++versionCarrito;
        $.getJSON('/restCarrito/obtenerProductos').done(function (productos) {
            if (version !== versionCarrito) return;
            const total = productos.reduce((s, p) => s + Number(p.cantidad || 0), 0);
            $('#contador_carrito, .contador-carrito-movil').text(total).prop('hidden', total <= 0);
        }).fail(function () { if (version === versionCarrito) $('#contador_carrito, .contador-carrito-movil').text('').prop('hidden', true); });
    };
    function agregarFavorito(id) {
        const botones = $('.favorito-agregar[data-id="' + id + '"]').prop('disabled', true);
        $.post('/restFavoritos', {id: id}).done(function () { favoritoPendiente = null; window.marcarFavoritoCatalogo?.(id, true); window.marcarFavoritoDetalle?.(id, true); LVLFeedback.notificar('Anadido a favoritos.', 'success'); }).fail(function (xhr) {
            if (xhr.status === 401) { favoritoPendiente = id; LVLFeedback.notificar('Inicia sesion para guardar favoritos.', 'warning'); irARuta('identificarme'); }
            else LVLFeedback.notificar('No se pudo guardar el favorito.', 'error');
        }).always(function () { botones.prop('disabled', false); });
    }
    $(document).on('click', '.favorito-agregar', function () {
        const boton = $(this), id = Number(boton.data('id'));
        if (boton.prop('disabled')) return;
        if ((boton.hasClass('product-favorito') || boton.hasClass('detalle-favorito')) && boton.attr('aria-pressed') === 'true') {
            boton.prop('disabled', true);
            $.ajax({url: '/restFavoritos', type: 'DELETE', data: {id: id}})
                .done(function () {
                    const card = boton.closest('#favoritos_resultados .product-card');
                    if (card.length) { card.remove(); if (!$('#favoritos_resultados .product-card').length) mostrarVacioFavoritos(); else $('#favoritos_titulo').trigger('focus'); }
                    window.marcarFavoritoCatalogo?.(id, false); window.marcarFavoritoDetalle?.(id, false);
                    LVLFeedback.notificar('Eliminado de favoritos.', 'success');
                })
                .fail(function () { LVLFeedback.notificar('No se pudo eliminar el favorito.', 'error'); })
                .always(function () { boton.prop('disabled', false); });
        } else agregarFavorito(id);
    });
    window.reanudarFavorito = function () { if (favoritoPendiente !== null) agregarFavorito(favoritoPendiente); };
    $(document).on('click', '.favorito-eliminar', function () { const boton = $(this); if (boton.prop('disabled')) return; boton.prop('disabled', true); $.ajax({url: '/restFavoritos', type: 'DELETE', data: {id: boton.data('id')}}).done(function () { LVLFeedback.notificar('Eliminado de favoritos.', 'success'); window.mostrarFavoritos(); }).fail(function () { LVLFeedback.notificar('No se pudo eliminar el favorito.', 'error'); }).always(function () { boton.prop('disabled', false); }); });
    function mostrarVacioFavoritos() {
        $('#favoritos_resultados').remove();
        $('#favoritos_estado').html('<p>Tu lista de favoritos esta vacia.</p><a class="btn btn-primary" href="#perfumes" data-ruta="perfumes">Explorar catalogo</a>').find('a').trigger('focus');
    }
    let versionFavoritosVista = 0;
    window.mostrarFavoritos = function () {
        const version = idNavegacion, peticion = ++versionFavoritosVista;
        $('#contenedor').html('<section class="favoritos-vista"><h1 id="favoritos_titulo" tabindex="-1">Favoritos</h1><div id="favoritos_estado" role="status"><p>Cargando favoritos...</p></div><div id="favoritos_resultados" class="catalogo-resultados"></div></section>');
        $.when(cargaPlantillaProductos, $.getJSON('/restFavoritos')).done(function (_plantilla, respuesta) {
            if (version !== idNavegacion || peticion !== versionFavoritosVista) return;
            const productos = respuesta[0];
            if (!productos.length) { mostrarVacioFavoritos(); return; }
            const datos = productos.map(p => ({ID:p.id, NOMBRE:p.nombre, MARCA:p.marca, FAMILIA:p.familiaOlfativa || '', PRECIO:Number(p.precio).toFixed(2).replace('.', ','), PRECIO_ORIGINAL:Number(p.precioOriginal).toFixed(2).replace('.', ','), DESCUENTO_PORCENTAJE:p.descuentoPorcentaje, TIENE_DESCUENTO:p.descuentoPorcentaje > 0}));
            $('#favoritos_estado').empty();
            $('#favoritos_resultados').html(Mustache.render(plantilla_productos, datos));
            productos.forEach(p => window.marcarFavoritoCatalogo(Number(p.id), true));
            window.iniciarGaleriasFavoritos(productos.map(p => p.id), version, () => idNavegacion);
        }).fail(function (xhr) {
            if (version !== idNavegacion || peticion !== versionFavoritosVista) return;
            if (xhr.status === 401) $('#favoritos_estado').html('<p>Inicia sesion para ver tus favoritos.</p><a class="btn btn-primary" href="#identificarme" data-ruta="identificarme">Iniciar sesion</a>');
            else $('#favoritos_estado').html('<p role="alert">No se pudieron cargar los favoritos.</p><button type="button" class="btn btn-secondary" id="reintentar-favoritos">Reintentar</button>');
        });
    };
    $(document).on('click', '#reintentar-favoritos', function () { window.mostrarFavoritos(); });
    window.mostrarNotas = function () {
        const version = idNavegacion;
        $.getJSON('/restPerfumes/notas').done(function (notas) { if (version !== idNavegacion) return; const lista = notas.map(n => Mustache.render('<li data-no-traducir>{{.}}</li>', n)).join(''); $('#contenedor').html('<section class="pagina-informativa"><h1>Notas olfativas</h1><p>Notas presentes en nuestro catalogo.</p>' + (lista ? '<ul>' + lista + '</ul>' : '<p>No hay notas disponibles.</p>') + '</section>'); }).fail(function () { if (version === idNavegacion) $('#contenedor').text(LVLI18n.t('No se pudieron cargar las notas.')); });
    };
    window.actualizarContadorCarrito();
})();
