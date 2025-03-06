package com.segundito.app.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Eliminar conflictos de mapeo
        registry.setOrder(Integer.MAX_VALUE - 1);

        // Recursos estáticos básicos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/css/**", "/js/**", "/img/**", "/favicon.ico")
                .addResourceLocations(
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/img/",
                        "classpath:/static/")
                .setCachePeriod(3600);

        // Configuración de archivos de uploads
        File uploadsDir = new File(uploadDir);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        String uploadPath = uploadsDir.getAbsolutePath();
        if (!uploadPath.endsWith(File.separator)) {
            uploadPath = uploadPath + File.separator;
        }

        System.out.println("📁 Ruta de uploads configurada: file:" + uploadPath);

        // Registrar manejador de uploads con alta prioridad
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(0)
                .setUseLastModified(true)
                .resourceChain(false);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Asegura que las rutas con extensiones no sean tratadas como solicitudes del controlador
        configurer.setUseTrailingSlashMatch(false);
        configurer.setUseSuffixPatternMatch(false);
    }

    // Aumentar tamaño del buffer de respuesta
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                connector.setMaxPostSize(1024 * 1024 * 10); // 10 MB
                // Aumentar el buffer de respuesta
                connector.setProperty("outputBufferSize", "32768");
            }
        };
    }
}