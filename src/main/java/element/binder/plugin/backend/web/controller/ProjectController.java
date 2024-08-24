package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ProjectControllerApi", description = "API для работы с проектами")
public interface ProjectController {

    @Operation(summary = "Создание проекта")
    ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectRequestDto request);

    @Operation(summary = "Редактирование проекта")
    ResponseEntity<ProjectResponseDto> updateProject(@Parameter(description = "Идентификатор проекта") UUID id,
                                                     @RequestBody ProjectRequestDto request);

    @Operation(summary = "Удаление проекта")
    ResponseEntity<UUID> deleteProject(@Parameter(description = "Идентификатор проекта") UUID id);

    @Operation(summary = "Получение всех проектов")
    ResponseEntity<List<ProjectResponseDto>> getProjects(@RequestBody DataTablesInput request);

    @Operation(summary = "Создание внутреннего проекта")
    ResponseEntity<InnerProjectResponseDto> createInnerProject(@Parameter(description = "идентификатор проекта") UUID id,
                                                               @RequestBody InnerProjectRequestDto request);

    @Operation(summary = "Получить все внутренние проекты по идентификатору проекта")
    ResponseEntity<List<InnerProjectResponseDto>> getAllInnerProjectsByProject(
            @Parameter(description = "Идентификатор проекта") UUID id);
}
