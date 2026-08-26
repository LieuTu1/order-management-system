package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {

        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/oauth2/authorization/google").permitAll()
                .requestMatchers("/api/users/verify-email").permitAll()

                //Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                // ADMIN
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/roles/**").hasRole("ADMIN")

                // ADMIN + STAFF
                .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "STAFF")

                //PRODUCTS
                .requestMatchers(HttpMethod.GET, "/api/products/**")
                .hasAnyRole("ADMIN", "STAFF")

                // ADMIN: được thêm, sửa, xóa sản phẩm
                .requestMatchers(HttpMethod.POST, "/api/products/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                .hasRole("ADMIN")

                //CATEGORIES
                .requestMatchers(HttpMethod.GET, "/api/categories/**")
                .hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(HttpMethod.POST, "/api/categories/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                .hasRole("ADMIN")

                //SUPPLIERS
                .requestMatchers(HttpMethod.GET, "/api/suppliers/**")
                .hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(HttpMethod.POST, "/api/suppliers/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/suppliers/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**")
                .hasRole("ADMIN")

                // Mọi request khác
                .anyRequest().authenticated()
        );

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
        );

        // BẬT OAuth2 Login
        http.oauth2Login(oauth2 ->
                oauth2.successHandler(oAuth2LoginSuccessHandler)
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception {
        return configuration.getAuthenticationManager() ;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Domain frontend production được truyền qua biến môi trường FRONTEND_URL trên Railway.
        // Nếu chưa set (chạy local), fallback về localhost:3000/3001 như cũ.
        String frontendUrl = System.getenv("FRONTEND_URL");

        List<String> allowedOrigins = new java.util.ArrayList<>(
                List.of("http://localhost:3000", "http://localhost:3001")
        );
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            allowedOrigins.add(frontendUrl);
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}