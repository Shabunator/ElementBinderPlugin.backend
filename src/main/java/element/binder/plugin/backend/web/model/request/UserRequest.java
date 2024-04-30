package element.binder.plugin.backend.web.model.request;

import element.binder.plugin.backend.validation.CheckEmail;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank(message = "Поле 'Имя организации' должно быть заполненно") String name,
                          @CheckEmail String email,
                          @NotBlank(message = "Поле 'Пароль' должно быть заполненно") String password) {
}
