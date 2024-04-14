package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PluginControllerApi", description = "API для работы с бэкендом")
public interface PluginController {

    @Operation(summary = "Сохранение элемента")
    @ApiResponse(responseCode = "200", description = "Успешное сохранение элемента",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ElementsResponse.class))})
    ResponseEntity<ElementsResponse> post(@RequestBody ElementRequest request);

    @Operation(summary = "Выгрузка элементов")
    @ApiResponse(responseCode = "200", description = "Успешная выгрузка элементов",
            content = {@Content(mediaType = "application/pdf", schema = @Schema(implementation = byte[].class))})
    ResponseEntity<byte[]> load();
}
