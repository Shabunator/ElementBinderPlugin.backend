package element.binder.plugin.backend.mapper;

import element.binder.plugin.backend.entity.Element;
import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.service.InnerProjectService;
import element.binder.plugin.backend.web.model.request.ElementRequest;
import element.binder.plugin.backend.web.model.response.ElementResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface ElementMapper {

    ElementMapper INSTANCE = Mappers.getMapper(ElementMapper.class);

    List<ElementResponse> elementsToElementResponseList(Set<Element> elements);

    ElementResponse elementToElementResponse(Element element);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagesUrl", source = "fileUrls")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "article", source = "request.article")
    @Mapping(target = "size", source = "request.size")
    @Mapping(target = "materialName", source = "request.materialName")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "innerProject", source = "request.innerProjectId", qualifiedByName = "mapInnerProject")
    Element elementRequestToElement(ElementRequest request, List<String> fileUrls, @Context InnerProjectService innerProjectService);

    @Named("mapInnerProject")
    default InnerProject map(UUID innerProjectId, @Context InnerProjectService innerProjectService) {
        return innerProjectService.findProjectById(innerProjectId);
    }
}
