package element.binder.plugin.backend.mapper;

import element.binder.plugin.backend.entity.Project;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectResponseDto projectToProjectResponseDto(Project project);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "innerProjects", ignore = true)
    Project mapToProject(ProjectRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "innerProjects", ignore = true)
    Project updateProjectFromProjectRequestDto(ProjectRequestDto request, @MappingTarget Project project);
}
