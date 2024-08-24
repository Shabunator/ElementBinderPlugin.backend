package element.binder.plugin.backend.service;

import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.exception.EntityNotFoundException;
import element.binder.plugin.backend.mapper.InnerProjectMapper;
import element.binder.plugin.backend.repository.InnerProjectRepository;
import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InnerProjectService {

    private final MinioService minioService;
    private final ProjectService projectService;
    private final InnerProjectRepository innerProjectRepository;
    private final InnerProjectMapper mapper = InnerProjectMapper.INSTANCE;

    private static final String INNER_PROJECT_NOT_FOUND = "Внутренний проект с ID = %d не найден";

    @Transactional
    public InnerProjectResponseDto create(UUID projectId, InnerProjectRequestDto request) {
        checkInnerProjectName(request.name());

        var innerProject = mapper.innerProjectRequestDtoToInnerProject(projectId, request, projectService);
        innerProjectRepository.save(innerProject);
        log.debug("Создан внутренний проект с ID = {}", innerProject.getId());

        return mapper.innerProjectToInnerProjectResponseDto(innerProject);
    }

    @Transactional
    public InnerProjectResponseDto update(UUID id, InnerProjectRequestDto request) {
        checkInnerProjectName(request.name());

        var innerProject = innerProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(INNER_PROJECT_NOT_FOUND, id)));

        String bucketName = minioService.checkBucket();
        String currentFolderName = innerProject.getName();
        String newFolderName = request.name();

        if (newFolderName != null && !newFolderName.isEmpty() && !newFolderName.equals(currentFolderName)) {
            minioService.renameFolder(bucketName, currentFolderName, newFolderName);
        }

        mapper.updateInnerProjectFromInnerProjectRequestDto(request);
        innerProjectRepository.save(innerProject);
        log.debug("Обновлены данные внутреннего проекта с ID = {}", innerProject.getId());

        return mapper.innerProjectToInnerProjectResponseDto(innerProject);
    }

    @Transactional
    public UUID delete(UUID id) {
        var innerProject = innerProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(INNER_PROJECT_NOT_FOUND, id)));

        String bucketName = minioService.checkBucket();
        String projectName = innerProject.getProject().getName();
        String innerProjectFolderName = innerProject.getName();
        String folderPath = projectName + "/" + innerProjectFolderName;

        innerProjectRepository.delete(innerProject);
        minioService.deleteFolder(bucketName, folderPath);
        log.debug("Удален внутренний проект с ID = {}", innerProject.getId());
        return id;
    }

    public List<InnerProjectResponseDto> getAll(DataTablesInput request) {
        var innerProjects = innerProjectRepository.findAll(request);
        if (innerProjects.getData().isEmpty()) {
            log.warn("Таблица внутренних проектов пуста");
        } else {
            log.debug("Получение всех внутренних проектов. Количество записей = {}", innerProjects.getRecordsFiltered());
        }
        return innerProjects.getData().stream()
                .map(mapper::innerProjectToInnerProjectResponseDto)
                .collect(Collectors.toList());
    }

    public List<InnerProjectResponseDto> getAllByProjectId(UUID projectId) {
        var project = projectService.findProjectById(projectId);
        var innerProjectList = innerProjectRepository.findAllByProject(project)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Inner Project not found by ProjectId = %s", projectId)));
        return mapper.innerProjectListToInnerProjectResponseDto(innerProjectList);
    }

    public InnerProject findProjectById(UUID innerProjectId) {
        return innerProjectRepository.findById(innerProjectId)
                .orElseThrow(() -> new EntityNotFoundException("Inner Project not found with id: " + innerProjectId));
    }

    private void checkInnerProjectName(String innerProjectName) {
        if (innerProjectRepository.existsByName(innerProjectName)) {
            throw new EntityNotFoundException("Inner Project with this name " + "[" + innerProjectName + "]" + " is existing!");
        }
    }
}
