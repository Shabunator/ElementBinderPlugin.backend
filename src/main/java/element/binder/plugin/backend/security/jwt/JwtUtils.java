package element.binder.plugin.backend.security.jwt;

import element.binder.plugin.backend.properties.JwtProperties;
import element.binder.plugin.backend.security.AppUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@SuppressWarnings("CommentsIndentation")
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String generateJwtToken(AppUserDetails userPrincipal) {

        var roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return generateJwtToken(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles);
    }

    public String generateJwtToken(UUID id, String name, String email, List<String> roles) {

        long now = System.currentTimeMillis();
        long expirationTime = now + jwtProperties.getAccessTokenExpiration().toMillis();

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claims(fillClaims(name, email, roles))
                .issuedAt(new Date(now))
                .expiration(new Date(expirationTime))
                .signWith(getSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public boolean validate(String authToken) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    private Map<String, Object> fillClaims(String name, String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", name);
        claims.put("email", email);
        claims.put("roles", roles);
        return claims;
    }

    private SecretKey getSecretKey() {
        var secretKey = Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes());
        secretKey.getAlgorithm();
        return secretKey;
    }
}
