package element.binder.plugin.backend.web.model.response;

import java.util.UUID;

public record ElementsResponse(UUID id, String name, String article, String size, String materialName, Double price) {
}
