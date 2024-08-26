package element.binder.plugin.backend.web.model.response;

public record ElementResponse(String name,
                               String minioUrl,
                               String article,
                               String size,
                               String materialName,
                               Double price) {
}
