package com.techcorp.helpdesk.config;

import com.techcorp.helpdesk.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UtilisateurService utilisateurService;

    // --- Encodeur de mot de passe BCrypt ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Fournisseur d'authentification ---
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(utilisateurService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // --- Gestionnaire d'authentification ---
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // --- Règles de sécurité HTTP ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Pages publiques
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Création d'utilisateur réservée à l'admin
                        .requestMatchers("/utilisateurs/**").hasRole("ADMIN")
                        // Suppression réservée à l'admin
                        .requestMatchers("/tickets/*/supprimer").hasRole("ADMIN")
                        // Assignation réservée aux techniciens et admins
                        .requestMatchers("/tickets/*/assigner").hasAnyRole("TECHNICIEN", "ADMIN")
                        // Tout le reste nécessite d'être connecté
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/tickets", true)
                        .failureUrl("/login?erreur=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?deconnecte=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}