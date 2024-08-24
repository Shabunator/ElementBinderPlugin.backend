package element.binder.plugin.backend.mapper;

import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.entity.Project;
import element.binder.plugin.backend.service.ProjectService;
import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface InnerProjectMapper {

    InnerProjectMapper INSTANCE = Mappers.getMapper(InnerProjectMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "projectId")
    @Mapping(target = "elements", ignore = true)
    InnerProject innerProjectRequestDtoToInnerProject(UUID projectId,
                                                      InnerProjectRequestDto requestDto,
                                                      @Context ProjectService projectService);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "elements", ignore = true)
    InnerProject updateInnerProjectFromInnerProjectRequestDto(InnerProjectRequestDto requestDto);

    List<InnerProjectResponseDto> innerProjectListToInnerProjectResponseDto(List<InnerProject> innerProjects);

    InnerProjectResponseDto innerProjectToInnerProjectResponseDto(InnerProject innerProject);

    default Project map(UUID projectId, @Context ProjectService projectService) {
        return projectService.findProjectById(projectId);
    }
}
