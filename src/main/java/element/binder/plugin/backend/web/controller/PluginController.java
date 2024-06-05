package element.binder.plugin.backend.web.controller;

import element.binder.plugin.backend.web.model.response.ElementsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "PluginControllerApi", description = "API для работы с элементами веб-страницы")
public interface PluginController {

    @Operation(summary = "Сохранение элемента")
    @ApiResponse(responseCode = "200", description = "Успешное сохранение элемента",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ElementsResponse.class))})
    ResponseEntity<ElementsResponse> post(@RequestParam("images") MultipartFile[] images,
                                          @RequestParam("name") String name,
                                          @RequestParam("article") String article,
                                          @RequestParam("size") String size,
                                          @RequestParam("materialName") String materialName,
                                          @RequestParam("price") Double price);

    @Operation(summary = "Выгрузка элементов")
    @ApiResponse(responseCode = "200", description = "Успешная выгрузка элементов",
            content = {@Content(mediaType = "application/pdf", schema = @Schema(implementation = byte[].class))})
    ResponseEntity<byte[]> load();
}
