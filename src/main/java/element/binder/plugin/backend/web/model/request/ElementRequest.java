package element.binder.plugin.backend.web.model.request;

public record ElementRequest(String name,
                             String article,
                             String size,
                             String materialName,
                             Double price) {
}
