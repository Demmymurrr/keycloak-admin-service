package ru.demmy.keycloak.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak getAdminClient(@Value("${keycloak.url}") String keycloakUrl,
                                   @Value("${keycloak.realm}") String realm,
                                   @Value("${keycloak.username}") String username,
                                   @Value("${keycloak.password}") String password,
                                   @Value("${keycloak.client-id}") String clientId) {

        return KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .build();
    }
}
