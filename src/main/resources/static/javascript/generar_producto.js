(() => {
    const boton = document.getElementById('generarProducto');
    const elegir = opciones => opciones[Math.floor(Math.random() * opciones.length)];
    const asignar = (id, valor) => { document.getElementById(id).value = valor; };
    const elegirSelect = id => {
        const select = document.getElementById(id);
        const opciones = Array.from(select.options).filter(o => o.value && o.value !== '0');
        if (opciones.length) select.value = elegir(opciones).value;
    };
    boton.addEventListener('click', () => {
        asignar('nombre', elegir(['Brisa', 'Luz', 'Aura', 'Jardin', 'Sueno']) + ' ' + elegir(['Dorada', 'Nocturna', 'Suave', 'Celeste', 'Radiante']) + ' ' + Math.floor(10 + Math.random() * 90));
        asignar('descripcion', 'Fragancia de demostracion con caracter floral y amaderado.');
        asignar('familiaOlfativa', 'Floral amaderada');
        asignar('precio', (20 + Math.random() * 150).toFixed(2));
        asignar('descuentoPorcentaje', '0');
        asignar('stock', String(Math.floor(Math.random() * 100)));
        ['marca', 'concentracion', 'mililitros', 'genero', 'idCategoria'].forEach(elegirSelect);
        ['seleccionSalida', 'seleccionCorazon', 'seleccionFondo'].forEach(id => {
            const opciones = Array.from(document.getElementById(id).options);
            opciones.forEach(o => { o.selected = false; });
            const primera = elegir(opciones); primera.selected = true;
            const restantes = opciones.filter(o => o !== primera);
            if (restantes.length) elegir(restantes).selected = true;
        });
        document.getElementById('avisoGenerador').textContent = 'Datos ficticios de demostracion. Revisa los valores y selecciona una imagen si lo deseas.';
    });
})();
