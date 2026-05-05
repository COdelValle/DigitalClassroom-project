package cl.digitalclassroom.studentmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el microservicio Student Manager.
 * Define las políticas de autenticación, autorización y gestión de sesiones.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     *
     * @param http HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain configurada
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF por ser una API REST sin cookies de sesión
            .csrf(csrf -> csrf.disable())

            // Establece la política de creación de sesión como STATELESS
            // (No se crea sesión porque usamos tokens JWT o similar)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Autorización de requests
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a Swagger/OpenAPI
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/docs/**").permitAll()

                // Permite acceso a health check (actuator)
                .requestMatchers("/actuator/**").permitAll()

                // Por defecto, todas las demás requests requieren autenticación
                // Nota: En producción, se debería integrar JWT o OAuth2
                .anyRequest().authenticated()
            )

            // Habilita HTTP Basic (temporal, reemplazar con JWT en producción)
            .httpBasic(basic -> {});

        return http.build();
    }

    /**
     * Bean para codificación de contraseñas usando BCrypt.
     * Proporciona seguridad criptográfica para almacenar contraseñas.
     *
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

