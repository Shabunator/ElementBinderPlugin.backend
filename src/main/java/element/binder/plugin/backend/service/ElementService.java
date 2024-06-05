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
    private final MinioService minioService;
    private final ElementRepository repository;
    private final ElementMapper mapper = ElementMapper.INSTANCE;

    @Transactional
    public ElementsResponse post(ElementRequest request) {
        var element = mapper.elementRequestToElement(request);
        minioService.uploadFile(request.images());
        var saved = repository.save(element);
        return mapper.elementToElementResponse(saved);
    }

    public byte[] load() {
        var elements = repository.findAll();
        return pdfService.generatePdf(elements);
    }
}
