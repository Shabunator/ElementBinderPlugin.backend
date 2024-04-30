package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.security.service.SecurityService;
import element.binder.plugin.backend.web.controller.AuthController;
import element.binder.plugin.backend.web.model.request.LoginRequest;
import element.binder.plugin.backend.web.model.request.RefreshTokenRequest;
import element.binder.plugin.backend.web.model.request.UserRequest;
import element.binder.plugin.backend.web.model.response.AuthResponse;
import element.binder.plugin.backend.web.model.response.RefreshTokenResponse;
import element.binder.plugin.backend.web.model.response.SimpleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final SecurityService securityService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(securityService.register(userRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(securityService.logout());
    }
}
