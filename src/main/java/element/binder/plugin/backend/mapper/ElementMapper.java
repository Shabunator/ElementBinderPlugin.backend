package element.binder.plugin.backend.mapper;

import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.service.InnerProjectService;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementsResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface ElementMapper {

    ElementMapper INSTANCE = Mappers.getMapper(ElementMapper.class);

    ElementsResponse elementToElementResponse(Element element);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "article", target = "article")
    @Mapping(source = "size", target = "size")
    @Mapping(source = "materialName", target = "materialName")
    @Mapping(source = "price", target = "price")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(source = "innerProjectId", target = "innerProject", qualifiedByName = "mapInnerProject")
    Element elementRequestToElement(ElementRequest request, @Context InnerProjectService innerProjectService);

    @Named("mapInnerProject")
    default InnerProject map(UUID innerProjectId, @Context InnerProjectService innerProjectService) {
        return innerProjectService.findProjectById(innerProjectId);
    }
}
