package element.binder.plugin.backend.mapper;

import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElementMapper {

    ElementMapper INSTANCE = Mappers.getMapper(ElementMapper.class);

    ElementsResponse elementToElementResponse(Element element);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "article", target = "article")
    @Mapping(source = "size", target = "size")
    @Mapping(source = "materialName", target = "materialName")
    @Mapping(source = "price", target = "price")
    Element elementRequestToElement(ElementRequest request);
}
