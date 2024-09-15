package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.LoginRequest;
import element.binder.plugin.backend.web.model.request.RefreshTokenRequest;
import element.binder.plugin.backend.web.model.request.UserRequest;
import element.binder.plugin.backend.web.model.response.AuthResponse;
import element.binder.plugin.backend.web.model.response.RefreshTokenResponse;
import element.binder.plugin.backend.web.model.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "AuthControllerApi", description = "API для регистрации и авторизации")
public interface AuthController {

    @Operation(summary = "Авторизация пользователя")
    ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest);

    @Operation(summary = "Регистрация пользователя")
    ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid UserRequest userRequest);

    @Operation(summary = "Обновление токена")
    ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @Operation(summary = "Выход из личного кабинета")
    ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails);
}
