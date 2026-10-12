package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Configuracion;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.servicios.ServicioSetUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
@Transactional
public class ServicioSetUpImplJPA implements ServicioSetUp {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void prepararRegistrosIniciales() {
        Configuracion configuracion = null;
        try {
            configuracion =
                    (Configuracion)
                            entityManager.createQuery(
                                            "select c from Configuracion c").
                                    getSingleResult();
        } catch (NoResultException e) {
            System.out.println(
                    "no se encontro un unico registro en la tabla de Configuracion");
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
            p1.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/1.jpg"));
            entityManager.persist(p1);

            Perfumes p2 = new Perfumes("Chanel N5", "Chanel", 135.0, "Floral aldehídica", 100, "Eau de Parfum", "Mujer", "El clásico atemporal de la perfumería");
            p2.setCategoria(cFlorales);
            p2.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/2.jpg"));
            entityManager.persist(p2);

            Perfumes p3 = new Perfumes("La Vie Est Belle", "Lancome", 90.0, "Floral gourmand", 75, "Eau de Parfum", "Mujer", "Dulce y luminoso, con iris y vainilla");
            p3.setCategoria(cOrientales);
            p3.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/3.jpg"));
            entityManager.persist(p3);

            Perfumes p4 = new Perfumes("Acqua di Gio", "Giorgio Armani", 80.0, "Acuática cítrica", 100, "Eau de Toilette", "Hombre", "Brisa marina y frescura mediterránea");
            p4.setCategoria(cCitricos);
            p4.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/4.jpg"));
            entityManager.persist(p4);

            Perfumes p5 = new Perfumes("Black Opium", "Yves Saint Laurent", 100.0, "Oriental vainilla", 90, "Eau de Parfum", "Mujer", "Café negro y vainilla, nocturno y adictivo");
            p5.setCategoria(cOrientales);
            p5.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/5.jpg"));
            entityManager.persist(p5);

            Perfumes p6 = new Perfumes("Invictus", "Paco Rabanne", 85.0, "Amaderada acuática", 100, "Eau de Toilette", "Hombre", "Deportivo y enérgico, con notas marinas");
            p6.setCategoria(cAmaderados);
            p6.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/6.jpg"));
            entityManager.persist(p6);

            Perfumes p7 = new Perfumes("Light Blue", "Dolce & Gabbana", 75.0, "Cítrica floral", 100, "Eau de Toilette", "Mujer", "Limón siciliano y manzana, puro verano");
            p7.setCategoria(cCitricos);
            p7.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/7.jpg"));
            entityManager.persist(p7);

            Perfumes p8 = new Perfumes("Eros", "Versace", 70.0, "Oriental fougère", 100, "Eau de Toilette", "Hombre", "Menta, manzana verde y vainilla");
            p8.setCategoria(cOrientales);
            p8.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/8.jpg"));
            entityManager.persist(p8);

            Perfumes p9 = new Perfumes("Libre", "Yves Saint Laurent", 105.0, "Floral lavanda", 90, "Eau de Parfum", "Mujer", "Lavanda y flor de azahar, libre y atrevido");
            p9.setCategoria(cFlorales);
            p9.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/9.jpg"));
            entityManager.persist(p9);

            Perfumes p10 = new Perfumes("Bleu de Chanel", "Chanel", 120.0, "Amaderada aromática", 100, "Eau de Parfum", "Hombre", "Elegante, con cítricos e incienso");
            p10.setCategoria(cAmaderados);
            p10.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/10.jpg"));
            entityManager.persist(p10);

            configuracion = new Configuracion();
            configuracion.setRegistrosIniciales(true);
            entityManager.persist(configuracion);
        }
    }

    private byte[] leerImagenInicial(String ruta_imagen) {
        byte[] info = null;
        try {
            URL url = new URL(ruta_imagen);
            info = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return info;
    }

}