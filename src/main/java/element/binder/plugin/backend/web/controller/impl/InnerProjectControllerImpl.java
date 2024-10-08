package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.service.ElementService;
import element.binder.plugin.backend.service.InnerProjectService;
import element.binder.plugin.backend.web.controller.InnerProjectController;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ElementResponse;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inner-project")
public class InnerProjectControllerImpl implements InnerProjectController {

    private final InnerProjectService innerProjectService;
    private final ElementService elementService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<InnerProjectResponseDto> updateInnerProject(@PathVariable("id") UUID id,
                                                                      @RequestBody @Valid InnerProjectRequestDto request) {
        return ResponseEntity.ok(innerProjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<UUID> deleteInnerProject(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(innerProjectService.delete(id));
    }

    @PostMapping(path = "/{id}/element", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ElementResponse> addElement(@PathVariable("id") UUID innerProjectId,
                                                      @RequestParam("images") MultipartFile[] images,
                                                      @RequestParam("name") String name,
                                                      @RequestParam("article") String article,
                                                      @RequestParam("size") String size,
                                                      @RequestParam("materialName") String materialName,
                                                      @RequestParam("price") Double price) {
        var request = new ElementRequest(innerProjectId, images, name, article, size, materialName, price);
        return ResponseEntity.ok(elementService.addElement(request));
    }

    @GetMapping("/{id}/excel-report")
    public ResponseEntity<byte[]> getExcelReport(@PathVariable("id") UUID innerProjectId) {
        var excelReport = elementService.generateExcelReport(innerProjectId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excel_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelReport);
    }

    @GetMapping("/{id}/elements")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ElementResponse>> getInnerProjectElements(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(elementService.getAllElementsByInnerProject(id));
    }
}
