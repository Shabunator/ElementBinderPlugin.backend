package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "UserControllerApi", description = "API для работы с пользователями")
public interface UserController {

    @Operation(summary = "Создание проекта пользователя")
    ResponseEntity<ProjectResponseDto> createProject(@PathVariable("id") UUID id,
                                                     @RequestBody @Valid ProjectRequestDto request);

    @Operation(summary = "Получить все проекты пользователя")
    ResponseEntity<List<ProjectResponseDto>> getUserProjects(
            @Parameter(description = "Идентификатор пользователя", in = ParameterIn.PATH) UUID id);
}
