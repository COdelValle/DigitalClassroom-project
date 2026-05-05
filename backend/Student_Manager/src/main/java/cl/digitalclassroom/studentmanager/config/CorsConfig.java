package cl.digitalclassroom.studentmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para permitir
 * que el frontend acceda a los endpoints del microservicio.
 */
@Configuration
public class CorsConfig {

    /**
     * Define la fuente de configuración de CORS.
     * Permite solicitudes desde localhost (desarrollo) y puede ampliarse para producción.
     *
     * @return CorsConfigurationSource configurada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (agregar dominios de producción según sea necesario)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // Frontend local (Vite)
            "http://localhost:5173",      // Puerto alternativo de Vite
            "http://localhost:8080"       // Otros servicios locales
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS",
            "PATCH"
        ));

        // Headers permitidos en la solicitud
        configuration.setAllowedHeaders(Arrays.asList(
            "*"
        ));

        // Headers expuestos en la respuesta
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization"
        ));

        // Permite incluir cookies/credenciales en las solicitudes
        configuration.setAllowCredentials(true);

        // Tiempo máximo de cache de la preflight request (en segundos)
        configuration.setMaxAge(3600L);

        // Aplicar configuración a todos los paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

