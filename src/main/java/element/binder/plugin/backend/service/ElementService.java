package element.binder.plugin.backend.service;

import element.binder.plugin.backend.mapper.ElementMapper;
import element.binder.plugin.backend.repository.ElementRepository;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElementService {

    private final PdfService pdfService;
    private final ExcelService excelService;
    private final MinioService minioService;
    private final InnerProjectService innerProjectService;
    private final ElementRepository repository;
    private final ElementMapper mapper = ElementMapper.INSTANCE;

    @Transactional
    public ElementsResponse addElement(ElementRequest request) {
        var innerProject = innerProjectService.findProjectById(request.innerProjectId());
        var project = innerProject.getProject();

        var folderPath = project.getName() + "/" + innerProject.getName();

        var element = mapper.elementRequestToElement(request, innerProjectService);
        minioService.uploadFile(request.images(), folderPath);
        var saved = repository.save(element);
        return mapper.elementToElementResponse(saved);
    }

    public byte[] generatePdfReport() {
        var elements = repository.findAll();
        return pdfService.generatePdf(elements);
    }

    public byte[] generateExcelReport() {
        var elements = repository.findAll();
        return excelService.generateExcel(elements);
    }
}
