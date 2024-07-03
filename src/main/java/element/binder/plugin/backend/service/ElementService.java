package element.binder.plugin.backend.service;

import element.binder.plugin.backend.mapper.ElementMapper;
import element.binder.plugin.backend.repository.ElementRepository;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElementService {

    private final PdfService pdfService;
    private final ExcelService excelService;
    private final MinioService minioService;
    private final InnerProjectService innerProjectService;
    private final ElementRepository elementRepository;
    private final ElementMapper mapper = ElementMapper.INSTANCE;

    @Transactional
    public ElementsResponse addElement(ElementRequest request) {
        var innerProject = innerProjectService.findProjectById(request.innerProjectId());
        var project = innerProject.getProject();

        var folderPath = project.getName() + "/" + innerProject.getName();

        var element = mapper.elementRequestToElement(request, innerProjectService);
        minioService.uploadFile(request.images(), folderPath);
        var saved = elementRepository.save(element);
        return mapper.elementToElementResponse(saved);
    }

    public byte[] generatePdfReport(UUID innerProjectId) {
        var innerProject = innerProjectService.findProjectById(innerProjectId);
        var elements = elementRepository.findAllByInnerProjectId(innerProject.getId());
        return pdfService.generatePdf(elements);
    }

    public byte[] generateExcelReport(UUID innerProjectId) {
        var innerProject = innerProjectService.findProjectById(innerProjectId);
        var elements = elementRepository.findAllByInnerProjectId(innerProject.getId());
        return excelService.generateExcel(elements);
    }
}
