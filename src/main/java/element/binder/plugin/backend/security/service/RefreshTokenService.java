package element.binder.plugin.backend.security.service;

import element.binder.plugin.backend.entity.RefreshToken;
import element.binder.plugin.backend.exception.EntityNotFoundException;
import element.binder.plugin.backend.exception.RefreshTokenException;
import element.binder.plugin.backend.properties.JwtProperties;
import element.binder.plugin.backend.repository.RefreshTokenRepository;
import element.binder.plugin.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtProperties jwtProperties;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(UUID userId) {
        var refreshToken = RefreshToken.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!")))
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration().toMillis()))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Repeat signin action!");
        }

        return token;
    }

    @Transactional
    public void deleteByOrganizationId(UUID userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
