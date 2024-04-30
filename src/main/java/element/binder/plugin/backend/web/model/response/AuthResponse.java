package element.binder.plugin.backend.web.model.response;

public record AuthResponse(String token,
                           String refreshToken) {
}
