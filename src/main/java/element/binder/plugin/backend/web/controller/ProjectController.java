package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "ProjectControllerApi", description = "API для работы с проектами")
public interface ProjectController {

    @Operation(summary = "Изменить проект")
    ResponseEntity<ProjectResponseDto> updateProject(@PathVariable("id") UUID id,
                                                     @RequestBody @Valid ProjectRequestDto request);

    @Operation(summary = "Удалить проект")
    ResponseEntity<UUID> deleteProject(@PathVariable("id") UUID id);


    @Operation(summary = "Получить проект по идентификатору")
    ResponseEntity<ProjectResponseDto> getProject(@PathVariable("id") UUID id);

    @Operation(summary = "Создание внутреннего проекта")
    ResponseEntity<InnerProjectResponseDto> createInnerProject(@Parameter(description = "идентификатор проекта") UUID id,
                                                               @RequestBody InnerProjectRequestDto request);

    @Operation(summary = "Получить все внутренние проекты по идентификатору проекта")
    ResponseEntity<List<InnerProjectResponseDto>> getAllInnerProjectsByProject(
            @Parameter(description = "Идентификатор проекта") UUID id);

    @Operation(summary = "Добавить заглавное изображение проекта")
    ResponseEntity<String> addProjectPicture(@PathVariable UUID id,
                                             @RequestParam("image") MultipartFile[] images);

    @Operation(summary = "Удалить заглавное изображение проекта")
    ResponseEntity<String> removeProjectPicture(@PathVariable UUID id,
                                                @RequestParam("fileUrl") String imageUrl);

}
