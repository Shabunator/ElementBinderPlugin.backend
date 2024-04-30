package element.binder.plugin.backend.exception;

import java.text.MessageFormat;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String token, String message) {
        super(MessageFormat.format("Error trying to refresh by token: {0}: {1}", token, message));
    }
}
