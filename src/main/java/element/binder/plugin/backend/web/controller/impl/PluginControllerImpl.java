package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.service.ElementService;
import element.binder.plugin.backend.web.controller.PluginController;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plugin")
public class PluginControllerImpl implements PluginController {

    private final ElementService elementService;

    @PostMapping
    public ResponseEntity<ElementsResponse> post(@RequestBody ElementRequest request) {
        return ResponseEntity.ok(elementService.post(request));
    }

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
