package ru.demmy.keycloak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import ru.demmy.keycloak.security.JwtParser;
import ru.demmy.keycloak.security.KeycloakAuthenticationConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private JwtParser jwtParser;

    @Bean
    public KeycloakAuthenticationConverter keycloakAuthenticationConverter() {
        return new KeycloakAuthenticationConverter(jwtParser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(it -> it
                        .jwt()
                        .jwtAuthenticationConverter(keycloakAuthenticationConverter())
                )
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
