package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ElementResponse;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "InnerProjectControllerApi", description = "API для работы с внутренними проектами")
public interface InnerProjectController {

    @Operation(summary = "Редактирование внутреннего проекта")
    ResponseEntity<InnerProjectResponseDto> updateInnerProject(@Parameter(description = "идентификатор внутреннего проекта") UUID id,
                                                               @RequestBody InnerProjectRequestDto request);

    @Operation(summary = "Удаление внутреннего проекта")
    ResponseEntity<UUID> deleteInnerProject(@Parameter(description = "Идентификатор проекта") UUID id);

    @Operation(summary = "Сохранение элемента")
    ResponseEntity<ElementResponse> addElement(@PathVariable("id") UUID innerProjectId,
                                          @RequestParam("images") MultipartFile[] images,
                                          @RequestParam("name") String name,
                                          @RequestParam("article") String article,
                                          @RequestParam("size") String size,
                                          @RequestParam("materialName") String materialName,
                                          @RequestParam("price") Double price);

    @Operation(summary = "Сформировать отчет в формате Excel")
    ResponseEntity<byte[]> getExcelReport(@PathVariable("id") UUID innerProjectId);

    @Operation(summary = "Получить все элементы по внутреннему проекту")
    ResponseEntity<List<ElementResponse>> getInnerProjectElements(@PathVariable("id") UUID id);
}
