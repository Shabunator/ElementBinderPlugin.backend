package element.binder.plugin.backend.web.model.response;

import java.util.UUID;

public record ProjectResponseDto (UUID id, String name, String description) {
}
