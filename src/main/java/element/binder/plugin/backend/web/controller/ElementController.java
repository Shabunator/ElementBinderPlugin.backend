package element.binder.plugin.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "PluginControllerApi", description = "API для работы с элементами веб-страницы")
public interface ElementController {

    @Operation(summary = "Выгрузка элементов")
    ResponseEntity<byte[]> load();
}
