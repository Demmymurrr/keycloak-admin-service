package ru.demmy.keycloak.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.demmy.keycloak.enums.RealmRole;
import ru.demmy.keycloak.model.UserModel;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class KeycloakAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtParser jwtParser;

    @Override
    @SneakyThrows
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        String tokenValue = jwt.getTokenValue();
        UserModel userModel = jwtParser.parseJwtIntoUserModel(tokenValue);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (RealmRole realmRole : userModel.getRealmRoles()) {
            authorities.add(new SimpleGrantedAuthority(realmRole.getName()));
        }
        return new KeycloakAuthenticationToken(jwt, userModel, authorities);
    }

}
