package element.binder.plugin.backend.web.handler;

import element.binder.plugin.backend.exception.EntityNotFoundException;
import element.binder.plugin.backend.exception.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponseBody> runtimeExceptionHandler(RuntimeException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = MinioException.class)
    public ResponseEntity<ErrorResponseBody> minioExceptionHandler(MinioException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseBody> entityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    String fieldName = error.getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                    log.error("Ошибка валидации поля {}: {}", fieldName, errorMessage);
                });

        return ResponseEntity.badRequest().body(errors);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus status, Exception ex, WebRequest request) {
        return ResponseEntity.status(status)
                .body(ErrorResponseBody.builder()
                        .message(ex.getMessage())
                        .descriptions(request.getDescription(false))
                        .build());
    }
}
