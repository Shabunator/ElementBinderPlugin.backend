package element.binder.plugin.backend.security.service;

import element.binder.plugin.backend.entity.RefreshToken;
import element.binder.plugin.backend.entity.RoleType;
import element.binder.plugin.backend.entity.User;
import element.binder.plugin.backend.exception.AlreadyExistsException;
import element.binder.plugin.backend.exception.RefreshTokenException;
import element.binder.plugin.backend.repository.UserRepository;
import element.binder.plugin.backend.security.AppUserDetails;
import element.binder.plugin.backend.security.jwt.JwtUtils;
import element.binder.plugin.backend.web.model.request.LoginRequest;
import element.binder.plugin.backend.web.model.request.RefreshTokenRequest;
import element.binder.plugin.backend.web.model.request.UserRequest;
import element.binder.plugin.backend.web.model.response.AuthResponse;
import element.binder.plugin.backend.web.model.response.RefreshTokenResponse;
import element.binder.plugin.backend.web.model.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new AuthResponse(jwtUtils.generateJwtToken(userDetails), refreshToken.getToken());
    }

    @Transactional
    public SimpleResponse register(UserRequest organizationRequest) {

        if (userRepository.existsByName(organizationRequest.name())) {
            log.info("Организация с name = {} уже существует", organizationRequest.name());
            throw new AlreadyExistsException("Пользователь с таким именем уже существует!");
        }

        if (userRepository.existsByEmail(organizationRequest.email())) {
            log.info("Организация с email = {} уже существует", organizationRequest.email());
            throw new AlreadyExistsException("Пользователь с таким email уже существует!");
        }

        var user = User.builder()
                .name(organizationRequest.name())
                .email(organizationRequest.email())
                .password(passwordEncoder.encode(organizationRequest.password()))
                .build();
        user.setRoles(Set.of(RoleType.USER));
        var savedUser = userRepository.save(user);
        log.debug("Зарегистрирован пользователь с ID = {} и NAME = {}", savedUser.getId(), savedUser.getName());

        return new SimpleResponse("Пользователь зарегистрирован!");
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUser)
                .map(user -> {
                    refreshTokenService.deleteByOrganizationId(user.getId());
                    var roles = user.getRoles().stream()
                            .map(Objects::toString)
                            .toList();
                    String token = jwtUtils.generateJwtToken(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            roles);
                    var newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

                    return new RefreshTokenResponse(token, newRefreshToken);
                })
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found!"));
    }

    public SimpleResponse logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentPrincipal instanceof AppUserDetails userDetails) {
            UUID userId = userDetails.getId();
            var userName = userDetails.getUsername();
            refreshTokenService.deleteByOrganizationId(userId);
            log.info("Пользователь {} произвёл выход из личного кабинета!", userName);
        }
        return new SimpleResponse("Пользователь произвел выход из личного кабинета!");
    }
}
