package element.binder.plugin.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "ElementControllerApi", description = "API для работы с элементами проекта")
public interface ElementController {

    @Operation(summary = "Сформировать отчет в формате PDF")
    ResponseEntity<byte[]> getPdfReport();

    @Operation(summary = "Сформировать отчет в формате Excel")
    ResponseEntity<byte[]> getExcelReport();
}
