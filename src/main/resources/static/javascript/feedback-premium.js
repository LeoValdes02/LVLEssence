(function () {
    'use strict';
    if (window.LVLFeedback) return;
    const traducir = texto => window.LVLI18n ? window.LVLI18n.t(texto) : texto;
    let region, ultimo = '', dialogoActivo = false;
    const pendientes = [];
    const crear = (tag, clase, texto) => { const nodo = document.createElement(tag); if (clase) nodo.className = clase; if (texto != null) nodo.textContent = texto; return nodo; };
    function notificar(mensaje, tipo = 'info') {
        tipo = ['success', 'error', 'warning', 'info'].includes(tipo) ? tipo : 'info';
        const texto = traducir(String(mensaje));
        if (!region) { region = crear('div', 'feedback-toasts'); region.setAttribute('aria-label', traducir('Notificaciones')); document.body.append(region); }
        const clave = tipo + ':' + texto;
        if (ultimo === clave && (pendientes.some(n => n.clave === clave) || Array.from(region.children).some(n => n.dataset.clave === clave))) return;
        ultimo = clave;
        if (region.children.length >= 3) { pendientes.push({mensaje, tipo, clave}); return; }
        const toast = crear('div', 'feedback-toast feedback-' + tipo); toast.dataset.clave = clave;
        toast.setAttribute('role', tipo === 'error' ? 'alert' : 'status');
        toast.dataset.noTraducir = '';
        const icono = crear('span', 'feedback-icono', {success:'✓', error:'!', warning:'!', info:'i'}[tipo]); icono.setAttribute('aria-hidden', 'true');
        const cerrar = crear('button', 'feedback-cerrar', '×'); cerrar.type = 'button'; cerrar.setAttribute('aria-label', traducir('Cerrar notificación'));
        let timer; const quitar = () => { clearTimeout(timer); toast.remove(); if (pendientes.length) { const siguiente = pendientes.shift(); ultimo = ''; notificar(siguiente.mensaje, siguiente.tipo); } };
        cerrar.addEventListener('click', quitar);
        toast.append(icono, crear('span', 'feedback-texto', texto), cerrar); region.append(toast);
        if (tipo !== 'error' && tipo !== 'warning') timer = setTimeout(quitar, 5000);
    }
    function confirmarAccion(opciones) {
        if (dialogoActivo) return Promise.resolve(false);
        const o = opciones || {}, titulo = traducir(o.titulo || 'Confirmar acción'), descripcion = traducir(o.descripcion || '¿Deseas continuar?');
        if (typeof HTMLDialogElement === 'undefined' || !HTMLDialogElement.prototype.showModal) return Promise.resolve(window.confirm(titulo + '\n\n' + descripcion));
        dialogoActivo = true;
        return new Promise(resolve => {
            const anterior = document.activeElement, dialogo = crear('dialog', 'feedback-dialogo');
            dialogo.setAttribute('aria-labelledby', 'feedback-titulo'); dialogo.setAttribute('aria-describedby', 'feedback-descripcion'); dialogo.dataset.noTraducir = '';
            const tituloEl = crear('h2', '', titulo); tituloEl.id = 'feedback-titulo';
            const descripcionEl = crear('p', '', descripcion); descripcionEl.id = 'feedback-descripcion';
            const acciones = crear('div', 'feedback-acciones');
            const cancelar = crear('button', 'feedback-boton feedback-secundario', traducir('Cancelar')); cancelar.type = 'button';
            const aceptar = crear('button', 'feedback-boton ' + (o.destructivo ? 'feedback-destructivo' : 'feedback-primario'), traducir(o.confirmar || 'Confirmar')); aceptar.type = 'button';
            let entrada;
            dialogo.append(tituloEl, descripcionEl);
            if (o.textoConfirmacion) {
                const etiqueta = crear('label', 'feedback-etiqueta', traducir('Escribe el texto para confirmar')); entrada = crear('input', 'feedback-entrada'); entrada.type = 'text'; etiqueta.append(entrada); dialogo.append(etiqueta); aceptar.disabled = true;
                entrada.addEventListener('input', () => { aceptar.disabled = entrada.value !== o.textoConfirmacion; });
            }
            acciones.append(cancelar, aceptar); dialogo.append(acciones); document.body.append(dialogo);
            let resultado = false;
            cancelar.addEventListener('click', () => dialogo.close());
            aceptar.addEventListener('click', () => { resultado = true; dialogo.close(); });
            dialogo.addEventListener('close', () => { dialogo.remove(); dialogoActivo = false; if (anterior && anterior.isConnected) anterior.focus(); resolve(resultado); }, {once:true});
            dialogo.showModal(); cancelar.focus();
        });
    }
    let contadorErrores = 0;
    function mostrarErrorCampo(campo, mensaje) {
        if (!campo) return;
        let error = campo._feedbackError;
        if (!error || !error.isConnected) { error = crear('span', 'field-error'); error.id = 'feedback-campo-' + (++contadorErrores); campo.insertAdjacentElement('afterend', error); campo._feedbackError = error; }
        error.textContent = traducir(String(mensaje)); campo.setAttribute('aria-invalid', 'true');
        const ids = new Set((campo.getAttribute('aria-describedby') || '').split(/\s+/).filter(Boolean)); ids.add(error.id); campo.setAttribute('aria-describedby', Array.from(ids).join(' '));
        if (!campo._feedbackLimpieza) { campo._feedbackLimpieza = true; campo.addEventListener('input', function () {
            const aviso = campo._feedbackError; if (!aviso) return;
            const restantes = (campo.getAttribute('aria-describedby') || '').split(/\s+/).filter(id => id && id !== aviso.id);
            if (restantes.length) campo.setAttribute('aria-describedby', restantes.join(' ')); else campo.removeAttribute('aria-describedby');
            campo.removeAttribute('aria-invalid'); aviso.remove(); campo._feedbackError = null;
        }); }
    }
    window.LVLFeedback = Object.freeze({notificar, confirmarAccion, mostrarErrorCampo});
    function vincularErroresExistentes() {
        let primero = null;
        document.querySelectorAll('form .field-error').forEach(error => {
            const grupo = error.closest('.form-group, .form-field');
            const campo = grupo && grupo.querySelector('input:not([type=hidden]), select, textarea');
            if (!campo) return;
            if (!error.id) error.id = 'feedback-campo-' + (++contadorErrores);
            const ids = new Set((campo.getAttribute('aria-describedby') || '').split(/\s+/).filter(Boolean)); ids.add(error.id);
            campo.setAttribute('aria-describedby', Array.from(ids).join(' ')); campo.setAttribute('aria-invalid', 'true');
            if (!primero && !campo.disabled && campo.type !== 'hidden') primero = campo;
        });
        if (primero) primero.focus();
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', vincularErroresExistentes, {once:true}); else vincularErroresExistentes();
    document.addEventListener('submit', async function (e) {
        const form = e.target.closest('form[data-confirmar]'); if (!form) return;
        if (form.dataset.confirmado === 'reenviando') { form.dataset.confirmado = 'enviado'; return; }
        if (form.dataset.confirmado === 'enviado') { e.preventDefault(); return; }
        e.preventDefault();
        if (form.dataset.confirmando) return;
        form.dataset.confirmando = 'si'; const iniciador = e.submitter;
        const aceptado = await confirmarAccion({titulo: form.dataset.titulo || 'Confirmar acción', descripcion: form.dataset.confirmar, confirmar: form.dataset.accion || 'Eliminar', destructivo: form.dataset.destructivo !== 'false'});
        delete form.dataset.confirmando;
        if (!aceptado || !form.isConnected) return;
        form.dataset.confirmado = 'reenviando';
        if (iniciador && iniciador.form === form && !iniciador.disabled) form.requestSubmit(iniciador); else form.requestSubmit();
        if (!form.checkValidity()) delete form.dataset.confirmado;
    });
})();
