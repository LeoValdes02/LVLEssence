package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.servicios.ServicioSetUp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public CommandLineRunner cargarDatosIniciales(ServicioSetUp servicioSetUp) {

        return args -> {

            // El catálogo y el pedido de ejemplo se preparan de forma independiente.
            servicioSetUp.prepararRegistrosIniciales();

            servicioSetUp.prepararPedidoEjemplo();
        };
    }
}
