package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.LoginRequest;
import element.binder.plugin.backend.web.model.request.RefreshTokenRequest;
import element.binder.plugin.backend.web.model.request.UserRequest;
import element.binder.plugin.backend.web.model.response.AuthResponse;
import element.binder.plugin.backend.web.model.response.RefreshTokenResponse;
import element.binder.plugin.backend.web.model.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PluginControllerApi", description = "API для регистрации и авторизации")
public interface AuthController {

    @Operation(summary = "Авторизация пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная авторизация пользователя",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))})
    ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest);

    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная регистрация пользователя",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SimpleResponse.class))})
    ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid UserRequest userRequest);

    @Operation(summary = "Обновление токена")
    @ApiResponse(responseCode = "200", description = "Успешное обновление токена",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenResponse.class))})
    ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @Operation(summary = "Выход из личного кабинета")
    @ApiResponse(responseCode = "200", description = "Успешный выход из личного кабинета",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SimpleResponse.class))})
    ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails);
}
