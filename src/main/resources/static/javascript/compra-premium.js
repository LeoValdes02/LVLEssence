(function () {
    'use strict';
    const contenedor = document.getElementById('contenedor');
    function actualizar() {
        const paso = contenedor.querySelector('#form_check_out_1') ? 1 : contenedor.querySelector('#form_check_out_2') ? 2 : contenedor.querySelector('#form_check_out_3') ? 3 : contenedor.querySelector('#boton_confirmar_pedido') ? 4 : 0;
        if (!paso || contenedor.querySelector('.checkout-progreso')) return;
        const nav = document.createElement('nav');
        nav.className = 'checkout-progreso';
        nav.setAttribute('aria-label', 'Progreso de compra');
        const lista = document.createElement('ol');
        const nombres = ['Envío', 'Pago simulado', 'Envoltorio', 'Confirmación'];
        nombres.forEach(function (nombre, i) {
            const item = document.createElement('li');
            item.className = i + 1 < paso ? 'completado' : i + 1 === paso ? 'actual' : 'pendiente';
            if (i + 1 === paso) item.setAttribute('aria-current', 'step');
            const numero = document.createElement('span'); numero.className = 'checkout-numero'; numero.textContent = i + 1;
            item.append(numero, document.createTextNode(' ' + nombre)); lista.appendChild(item);
        });
        nav.appendChild(lista);
        contenedor.querySelector('.auth-card').prepend(nav);
    }
    new MutationObserver(actualizar).observe(contenedor, {childList: true, subtree: true});
    document.addEventListener('lvl:idioma-cambiado', function () { contenedor.querySelector('.checkout-progreso')?.remove(); actualizar(); });
    actualizar();
})();
