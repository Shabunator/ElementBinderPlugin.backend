package element.binder.plugin.backend.web.model.response;

import java.util.List;

public record ElementResponse(String name,
                              List<String> imagesUrl,
                              String article,
                              String size,
                              String materialName,
                              Double price) {
}
