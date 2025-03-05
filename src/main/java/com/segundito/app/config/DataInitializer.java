package com.segundito.app.config;

import com.segundito.app.entity.Categoria;
import com.segundito.app.entity.EstadoProducto;
import com.segundito.app.entity.Rol;
import com.segundito.app.repository.CategoriaRepository;
import com.segundito.app.repository.EstadoProductoRepository;
import com.segundito.app.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      EstadoProductoRepository estadoProductoRepository,
                                      CategoriaRepository categoriaRepository) {
        return args -> {
            // Inicialización de roles
            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol(null, "ROLE_ADMIN", "Administrador del sistema", null, null, null));
                rolRepository.save(new Rol(null, "ROLE_USER", "Usuario regular", null, null, null));
                System.out.println("Roles inicializados correctamente");
            }

            // Inicialización de estados de producto
            if (estadoProductoRepository.count() == 0) {
                estadoProductoRepository.save(new EstadoProducto(null, "Nuevo", "Producto sin usar, en su embalaje original", null));
                estadoProductoRepository.save(new EstadoProducto(null, "Casi nuevo", "Usado pocas veces, en excelente estado", null));
                estadoProductoRepository.save(new EstadoProducto(null, "Buen estado", "Usado pero bien conservado", null));
                estadoProductoRepository.save(new EstadoProducto(null, "Usado", "Muestra signos claros de uso pero funcional", null));
                estadoProductoRepository.save(new EstadoProducto(null, "Para piezas", "No funciona correctamente, para repuestos", null));
                System.out.println("Estados de producto inicializados correctamente");
            }

            // Inicialización de categorías principales
            if (categoriaRepository.count() == 0) {
                categoriaRepository.save(new Categoria(null, "Motor", "Vehículos, accesorios y piezas", "fa-car", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Inmobiliaria", "Pisos, casas, terrenos y alquileres", "fa-home", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Empleo", "Ofertas y demandas de trabajo", "fa-briefcase", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Electrónica", "Móviles, ordenadores, tablets y otros dispositivos", "fa-laptop", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Hogar", "Muebles, electrodomésticos y decoración", "fa-couch", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Moda", "Ropa, complementos y calzado", "fa-tshirt", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Deportes", "Equipamiento, bicicletas y accesorios deportivos", "fa-futbol", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Hobby", "Instrumentos, coleccionables y ocio", "fa-gamepad", null, null, true, null, null, null));
                categoriaRepository.save(new Categoria(null, "Servicios", "Profesionales, cuidados y formación", "fa-tools", null, null, true, null, null, null));
                System.out.println("Categorías inicializadas correctamente");
            }
        };
    }
}