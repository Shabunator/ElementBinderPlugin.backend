package element.binder.plugin.backend.service;

import element.binder.plugin.backend.mapper.ElementMapper;
import element.binder.plugin.backend.repository.ElementRepository;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElementService {

    private final ExcelService excelService;
    private final MinioService minioService;
    private final InnerProjectService innerProjectService;
    private final ElementRepository elementRepository;
    private final ElementMapper elementMapper = ElementMapper.INSTANCE;

    @Transactional
    public ElementResponse addElement(ElementRequest request) {
        var innerProject = innerProjectService.findProjectById(request.innerProjectId());
        var project = innerProject.getProject();

        var folderPath = project.getName() + "/" + innerProject.getName();

        var fileUrls = minioService.uploadFile(request.images(), folderPath);
        var element = elementMapper.elementRequestToElement(request, fileUrls, innerProjectService);
        var saved = elementRepository.save(element);

        innerProject.addElement(saved);
        return elementMapper.elementToElementResponse(saved);
    }

    public List<ElementResponse> getAllElementsByInnerProject(UUID innerProjectId) {
        var innerProject = innerProjectService.findProjectById(innerProjectId);
        return elementMapper.elementsToElementResponseList(innerProject.getElements());
    }

    public byte[] generateExcelReport(UUID innerProjectId) {
        var innerProject = innerProjectService.findProjectById(innerProjectId);
        var elements = elementRepository.findAllByInnerProjectId(innerProject.getId());
        return excelService.generateExcel(elements);
    }
}
