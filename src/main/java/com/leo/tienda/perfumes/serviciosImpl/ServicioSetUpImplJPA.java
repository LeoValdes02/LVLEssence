package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.constantesPedidos.EstadosPedido;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;
import com.leo.tienda.perfumes.servicios.ServicioSetUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Transactional
public class ServicioSetUpImplJPA implements ServicioSetUp {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ServicioPedidos servicioPedidos;
    @Autowired private PasswordEncoder passwordEncoder;


    @Override
    public void prepararRegistrosIniciales() {

        Configuracion configuracion = null;
        try {
            configuracion = (Configuracion) entityManager.createQuery("select c from Configuracion c").getSingleResult();
        } catch (NoResultException e) {
            System.out.println("no se encontro un unico registro en la tabla de Configuracion");
        }
        if (configuracion == null || !configuracion.isRegistrosIniciales()) {
            Categoria cCitricos = new Categoria("Cítricos", "Frescos, ligeros y energéticos");
            Categoria cFlorales = new Categoria("Florales", "Aromas de flores y jardín");
            Categoria cAmaderados = new Categoria("Amaderados", "Cálidos, con notas de madera");
            Categoria cOrientales = new Categoria("Orientales", "Intensos, dulces y especiados");
            entityManager.persist(cCitricos);
            entityManager.persist(cFlorales);
            entityManager.persist(cAmaderados);
            entityManager.persist(cOrientales);

            Perfumes p1 = new Perfumes("Sauvage", "Dior", 95.0, "Amaderada aromática", 100, "Eau de Toilette", "Hombre", "Fresco y potente, con bergamota y pimienta");
            p1.setCategoria(cAmaderados);
            p1.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/1.jpg"));
            entityManager.persist(p1);

            Perfumes p2 = new Perfumes("Chanel N5", "Chanel", 135.0, "Floral aldehídica", 100, "Eau de Parfum", "Mujer", "El clásico atemporal de la perfumería");
            p2.setCategoria(cFlorales);
            p2.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/2.jpg"));
            entityManager.persist(p2);

            Perfumes p3 = new Perfumes("La Vie Est Belle", "Lancome", 90.0, "Floral gourmand", 75, "Eau de Parfum", "Mujer", "Dulce y luminoso, con iris y vainilla");
            p3.setCategoria(cOrientales);
            p3.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/3.jpg"));
            entityManager.persist(p3);

            Perfumes p4 = new Perfumes("Acqua di Gio", "Giorgio Armani", 80.0, "Acuática cítrica", 100, "Eau de Toilette", "Hombre", "Brisa marina y frescura mediterránea");
            p4.setCategoria(cCitricos);
            p4.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/4.jpg"));
            entityManager.persist(p4);

            Perfumes p5 = new Perfumes("Black Opium", "Yves Saint Laurent", 100.0, "Oriental vainilla", 90, "Eau de Parfum", "Mujer", "Café negro y vainilla, nocturno y adictivo");
            p5.setCategoria(cOrientales);
            p5.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/5.jpg"));
            entityManager.persist(p5);

            Perfumes p6 = new Perfumes("Invictus", "Paco Rabanne", 85.0, "Amaderada acuática", 100, "Eau de Toilette", "Hombre", "Deportivo y enérgico, con notas marinas");
            p6.setCategoria(cAmaderados);
            p6.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/6.jpg"));
            entityManager.persist(p6);

            Perfumes p7 = new Perfumes("Light Blue", "Dolce & Gabbana", 75.0, "Cítrica floral", 100, "Eau de Toilette", "Mujer", "Limón siciliano y manzana, puro verano");
            p7.setCategoria(cCitricos);
            p7.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/7.jpg"));
            entityManager.persist(p7);

            Perfumes p8 = new Perfumes("Eros", "Versace", 70.0, "Oriental fougère", 100, "Eau de Toilette", "Hombre", "Menta, manzana verde y vainilla");
            p8.setCategoria(cOrientales);
            p8.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/8.jpg"));
            entityManager.persist(p8);

            Perfumes p9 = new Perfumes("Libre", "Yves Saint Laurent", 105.0, "Floral lavanda", 90, "Eau de Parfum", "Mujer", "Lavanda y flor de azahar, libre y atrevido");
            p9.setCategoria(cFlorales);
            p9.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/9.jpg"));
            entityManager.persist(p9);

            Perfumes p10 = new Perfumes("Bleu de Chanel", "Chanel", 120.0, "Amaderada aromática", 100, "Eau de Parfum", "Hombre", "Elegante, con cítricos e incienso");
            p10.setCategoria(cAmaderados);
            p10.setImagenPortada(leerImagenInicial("http://localhost:8080/imagenes_productos_iniciales/10.jpg"));
            entityManager.persist(p10);


// =====================================================
// USUARIO DE PRUEBA
// =====================================================

            Usuario usuarioEjemplo = new Usuario("demo.pedidos@lvl-essence.test", "demo123", "Cliente", "Demo", "600123456");
            usuarioEjemplo.setPass(passwordEncoder.encode(usuarioEjemplo.getPass()));
            usuarioEjemplo.setPassCodificada(true);

            entityManager.persist(usuarioEjemplo);


// =====================================================
// CARRITO DE PRUEBA
// =====================================================

            Carrito carritoEjemplo = new Carrito();
            carritoEjemplo.setUsuario(usuarioEjemplo);

            entityManager.persist(carritoEjemplo);


// Primer perfume: 2 unidades de Sauvage
            ProductoCarrito producto1 = new ProductoCarrito();

            producto1.setCarrito(carritoEjemplo);
            producto1.setPerfume(p1);
            producto1.setCantidad(2);

            entityManager.persist(producto1);


// Segundo perfume: 1 unidad de Chanel N5
            ProductoCarrito producto2 = new ProductoCarrito();

            producto2.setCarrito(carritoEjemplo);
            producto2.setPerfume(p2);
            producto2.setCantidad(1);

            entityManager.persist(producto2);


// Guardar los registros antes de consultar el carrito
            entityManager.flush();


// =====================================================
// CHECKOUT AUTOMÁTICO DEL PEDIDO DE EJEMPLO
// =====================================================

// Paso 1: datos de envío
            servicioPedidos.procesarPaso1("Cliente Demo", "Calle de las Flores 12", "Madrid", "Madrid", "28001", "Entregar por la mañana", usuarioEjemplo.getId());

// Paso 2: pago simulado
            servicioPedidos.procesarPaso2("Cliente Demo", "visa_demo", "demo.pedidos@lvl-essence.test", usuarioEjemplo.getId());

// Paso 3: envoltorio de regalo
            servicioPedidos.procesarPaso3(true, usuarioEjemplo.getId());

// Paso 4: confirmar compra
            servicioPedidos.confirmarPedido(usuarioEjemplo.getId());

            System.out.println("LVL ESSENCE: pedido inicial creado correctamente");


            configuracion = new Configuracion();
            configuracion.setRegistrosIniciales(true);
            entityManager.persist(configuracion);
        }
    }


    @Override
    public void prepararPedidoEjemplo() {

        // 1. Comprobar si ya tenemos pedidos
        Long totalPedidos = entityManager.createQuery("SELECT COUNT(p) FROM Pedido p", Long.class).getSingleResult();

        if (totalPedidos > 0) {
            System.out.println("LVL ESSENCE: ya existen pedidos. No se crea otro.");
            return;
        }

        // 2. Buscar dos perfumes existentes
        List<Perfumes> perfumes = entityManager.createQuery("SELECT p FROM Perfumes p ORDER BY p.id", Perfumes.class).setMaxResults(2).getResultList();

        if (perfumes.size() < 2) {
            throw new IllegalStateException("Se necesitan dos perfumes para el pedido inicial");
        }

        // 3. Buscar o crear el usuario de demostración
        String email = "demo.pedidos@lvl-essence.test";

        List<Usuario> usuarios = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class).setParameter("email", email).getResultList();

        Usuario usuario;

        if (usuarios.isEmpty()) {

            usuario = new Usuario(email, "demo123", "Cliente", "Demo", "600123456");
            usuario.setPass(passwordEncoder.encode(usuario.getPass()));
            usuario.setPassCodificada(true);

            entityManager.persist(usuario);

        } else {
            usuario = usuarios.get(0);
        }

        // 4. Crear el pedido completado
        Pedido pedido = new Pedido();

        pedido.setUsuario(usuario);
        pedido.setEstado(EstadosPedido.Completado.name());

        // Datos de envío
        pedido.setNombreCompleto("Cliente Demo");
        pedido.setDireccion("Calle de las Flores 12");
        pedido.setProvincia("Madrid");
        pedido.setCiudad("Madrid");
        pedido.setCodigoPostal("28001");
        pedido.setInstruccionesEntrega("Entregar por la mañana");

        // Datos de pago simulado
        pedido.setTitularTarjeta("Cliente Demo");
        pedido.setTipoTarjeta("visa_demo");
        pedido.setEmailFacturacion(email);

        // Personalización del pedido
        pedido.setEnvoltorioRegalo(true);

        entityManager.persist(pedido);

        // 5. Añadir dos perfumes al pedido
        for (int i = 0; i < perfumes.size(); i++) {

            ProductoPedido productoPedido = new ProductoPedido();

            productoPedido.setPedido(pedido);
            productoPedido.setPerfume(perfumes.get(i));
            productoPedido.setCantidad(i == 0 ? 2 : 1);
            productoPedido.setPrecioUnitario(java.math.BigDecimal.valueOf(perfumes.get(i).getPrecio()).setScale(2, java.math.RoundingMode.HALF_UP));

            entityManager.persist(productoPedido);
        }

        System.out.println("LVL ESSENCE: pedido inicial creado correctamente");
    }


    private byte[] leerImagenInicial(String ruta_imagen) {

        String nombreArchivo = ruta_imagen.substring(ruta_imagen.lastIndexOf('/') + 1);

        String ruta = "static/imagenes_productos_iniciales/" + nombreArchivo;

        try (InputStream entrada = new ClassPathResource(ruta).getInputStream()) {

            return entrada.readAllBytes();

        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la imagen: " + nombreArchivo, e);
        }
    }


}
