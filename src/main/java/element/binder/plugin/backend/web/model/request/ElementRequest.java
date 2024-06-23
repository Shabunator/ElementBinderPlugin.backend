package element.binder.plugin.backend.web.model.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ElementRequest(UUID innerProjectId,
                             MultipartFile[] images,
                             String name,
                             String article,
                             String size,
                             String materialName,
                             Double price) {
}
