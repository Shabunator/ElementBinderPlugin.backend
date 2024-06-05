package element.binder.plugin.backend.web.model.request;

import org.springframework.web.multipart.MultipartFile;

public record ElementRequest(MultipartFile[] images,
                             String name,
                             String article,
                             String size,
                             String materialName,
                             Double price) {
}
