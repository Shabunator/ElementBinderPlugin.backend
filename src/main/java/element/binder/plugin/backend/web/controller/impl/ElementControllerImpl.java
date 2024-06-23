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
@RequestMapping("/api/v1/element")
public class ElementControllerImpl implements ElementController {

    private final ElementService elementService;

    @GetMapping("/pdf-report")
    public ResponseEntity<byte[]> getPdfReport() {
        var pdfReport = elementService.generatePdfReport();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pdf_report.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }

    @GetMapping("/excel-report")
    public ResponseEntity<byte[]> getExcelReport() {
        var excelReport = elementService.generateExcelReport();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excel_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelReport);
    }
}
