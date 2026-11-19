package proy.EmpleApp.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/** Permisos iniciales: registro público, catálogo autenticado y administración restringida. */
@Configuration
public class SeguridadConfiguracion {
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsApi() {
        // También cubre logout, procesado por un filtro antes de llegar a los controladores MVC.
        var cors = new org.springframework.web.cors.CorsConfiguration();
        cors.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        cors.setAllowCredentials(true);
        cors.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(java.util.List.of("Content-Type", "X-CSRF-TOKEN"));
        var fuente = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", cors);
        return fuente;
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService usuariosDeSeguridad(
            proy.EmpleApp.repositorio.IUsuarioRepositorio repositorio) {
        // Usa las cuentas existentes e impide crear el usuario aleatorio de Spring Boot.
        return correo -> repositorio.findByCorreo(correo.strip().toLowerCase(java.util.Locale.ROOT))
            .map(usuario -> org.springframework.security.core.userdetails.User
                .withUsername(usuario.getIdUsuario().toString())
                .password(usuario.getContrasenaHash()).authorities("CUENTA").build())
            .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                "Credenciales incorrectas"));
    }

    @Bean
    public HttpSessionSecurityContextRepository contextosDeSesion() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public HttpSessionCsrfTokenRepository tokensCsrf() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public SecurityFilterChain seguridad(HttpSecurity http, UsuarioActual actual,
            HttpSessionSecurityContextRepository contextos,
            HttpSessionCsrfTokenRepository tokens) throws Exception {
        http.cors(cors -> cors.configurationSource(corsApi()))
            .csrf(csrf -> csrf.csrfTokenRepository(tokens))
            .securityContext(contexto -> contexto.securityContextRepository(contextos))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .requestCache(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(permisos -> permisos
                .requestMatchers("/", "/index", "/error", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/login", "/api/usuario/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuario/csrf", "/api/rol").permitAll()
                .requestMatchers("/api/usuario/me").authenticated()
                .requestMatchers("/api/trabajador/me").access((auth, contexto) ->
                    new AuthorizationDecision(tieneRol(actual, auth.get(), "Trabajador")))
                .requestMatchers("/api/empleador/me").access((auth, contexto) ->
                    new AuthorizationDecision(tieneRol(actual, auth.get(), "Empleador")))
                .requestMatchers("/api/servicio/mis-publicaciones", "/api/servicio/mis-publicaciones/**")
                    .access((auth, contexto) -> new AuthorizationDecision(tieneRol(actual, auth.get(), "Empleador")))
                .requestMatchers(HttpMethod.GET, "/api/servicio", "/api/servicio/*",
                    "/api/tiposervicio", "/api/tiposervicio/*",
                    "/api/estadoservicio", "/api/estadoservicio/*").authenticated()
                // Incluye las rutas Thymeleaf: no pueden servir como acceso alternativo al CRUD.
                .anyRequest().access((auth, contexto) ->
                    new AuthorizationDecision(actual.esAdministrador(auth.get()))))
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint((request, response, ex) ->
                    errorJson(response, 401, "Debes iniciar sesión"))
                .accessDeniedHandler((request, response, ex) ->
                    errorJson(response, 403, "No tienes permiso o falta un token CSRF válido")))
            .logout(salida -> salida.logoutUrl("/api/usuario/logout")
                .invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, auth) -> response.setStatus(204)));
        return http.build();
    }

    private boolean tieneRol(UsuarioActual actual, org.springframework.security.core.Authentication auth,
            String rol) {
        try {
            actual.exigirRol(auth, rol);
            return true;
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            return false;
        }
    }

    private void errorJson(jakarta.servlet.http.HttpServletResponse response, int estado,
            String mensaje) throws java.io.IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
