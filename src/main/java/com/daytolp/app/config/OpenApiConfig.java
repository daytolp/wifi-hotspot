package com.daytolp.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para exponer la documentación
 * interactiva de los endpoints REST de la aplicación.
 * Ruta principal:  /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wifiHotspotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Wifi Hotspot")
                        .description("Servicios para gestión y consulta de puntos de acceso WiFi públicos")
                        .version("v1.0.0")
                        .contact(new Contact().name("Desarrollador").email("jfortizf20@gmail.com"))
                );
    }
}

