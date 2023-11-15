package ru.demmy.keycloak.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import ru.demmy.keycloak.model.UserModel;

import java.util.Collection;
import java.util.Map;

@Transient
public class KeycloakAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final String userId;

    public KeycloakAuthenticationToken(Jwt token,
                                       UserModel userModel,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(token, userModel, null, authorities);
        this.setAuthenticated(true);
        this.userId = token.getSubject();
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    public String getUserId() {
        return userId;
    }
}
