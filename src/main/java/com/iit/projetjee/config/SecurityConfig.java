package com.iit.projetjee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.iit.projetjee.filter.XssFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomUserDetailsService userDetailsService;
    private final XssFilter xssFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                         @Lazy CustomUserDetailsService userDetailsService,
                         XssFilter xssFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
        this.xssFilter = xssFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Routes publiques
                .requestMatchers("/", "/h2-console/**", "/css/**", "/js/**", "/images/**", 
                               "/api/auth/**", "/login", "/error", "/public/**").permitAll()
                // Routes admin dashboard
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Dashboards spécifiques (doivent être avant les routes CRUD générales)
                .requestMatchers("/etudiants/dashboard", "/etudiants/cours/**").hasAnyRole("ETUDIANT", "ADMIN")
                .requestMatchers("/formateurs/dashboard", "/formateur/dashboard").hasAnyRole("FORMATEUR", "ADMIN")
                // Routes CRUD - accessibles par ADMIN
                .requestMatchers("/etudiants/**", "/formateurs/**", "/cours/**", 
                               "/inscriptions/**", "/notes/**", "/sessions/**", "/seances/**",
                               "/specialites/**", "/groupes/**").hasRole("ADMIN")
                // API REST - nécessite authentification
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .csrf(csrf -> {
                // Configuration CSRF améliorée avec CookieCsrfTokenRepository
                CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                tokenRepository.setCookiePath("/");
                tokenRepository.setCookieName("XSRF-TOKEN");
                tokenRepository.setHeaderName("X-XSRF-TOKEN");
                
                // Handler pour supporter les requêtes asynchrones
                CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                requestHandler.setCsrfRequestAttributeName("_csrf");
                
                csrf.csrfTokenRepository(tokenRepository)
                     .csrfTokenRequestHandler(requestHandler)
                     .ignoringRequestMatchers("/h2-console/**", "/api/auth/**");
            })
            .headers(headers -> headers
                // Protection contre le clickjacking
                .frameOptions(frameOptions -> frameOptions.deny())
                // Content Security Policy pour prévenir XSS
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                        "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                        "img-src 'self' data: https:; " +
                        "font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                        "connect-src 'self'; " +
                        "frame-ancestors 'none';")
                )
                // X-Content-Type-Options pour empêcher le MIME sniffing
                .contentTypeOptions(contentTypeOptions -> {})
                // HTTP Strict Transport Security (HSTS)
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                )
                // Referrer Policy
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                })
            )
            .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Le UserDetailsService est maintenant géré par CustomUserDetailsService
    // qui charge les utilisateurs depuis la base de données

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
