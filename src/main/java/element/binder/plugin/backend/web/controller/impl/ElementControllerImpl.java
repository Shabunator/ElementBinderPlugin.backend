package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.service.ElementService;
import element.binder.plugin.backend.web.controller.ElementController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plugin")
public class ElementControllerImpl implements ElementController {

    private final ElementService elementService;

    @GetMapping
    public ResponseEntity<byte[]> load() {
        var pdfContent = elementService.load();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=elements_report.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}
