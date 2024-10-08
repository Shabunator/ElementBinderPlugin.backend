package element.binder.plugin.backend.service;

import element.binder.plugin.backend.entity.Project;
import element.binder.plugin.backend.exception.EntityNotFoundException;
import element.binder.plugin.backend.mapper.ProjectMapper;
import element.binder.plugin.backend.repository.ProjectRepository;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final String PROJECT_NOT_FOUND = "Проект с ID = %s не найден";

    private final UserService userService;
    private final MinioService minioService;
    private final ProjectRepository projectRepository;

    private final ProjectMapper mapper = ProjectMapper.INSTANCE;

    @Transactional
    public ProjectResponseDto create(UUID id, ProjectRequestDto request) {
        checkProjectName(request.name());
        var user = userService.getUserById(id);

        var project = projectRepository.save(mapper.mapToProject(request));

        // Добавление пользователю проекта и закрепление его за ним
        user.addProject(project);
        userService.saveUser(user);

        log.debug("Создан проект с ID = {}", project.getId());

        return mapper.projectToProjectResponseDto(project);
    }

    @Transactional
    public ProjectResponseDto update(UUID id, ProjectRequestDto request) {
        checkProjectName(request.name());

        var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROJECT_NOT_FOUND, id)));

        String oldProjectName = project.getName();

        mapper.updateProjectFromProjectRequestDto(request, project);
        projectRepository.save(project);

        if (!oldProjectName.equals(project.getName())) {
            var bucketName = minioService.checkBucket();
            minioService.renameFolder(bucketName, oldProjectName, project.getName());
        }

        log.debug("Обновлены данные проекта с ID = {}", project.getId());
        return mapper.projectToProjectResponseDto(project);
    }

    @Transactional
    public UUID delete(UUID id) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROJECT_NOT_FOUND, id)));
        projectRepository.deleteById(id);
        log.debug("Удален проект с ID = {}", id);

        var bucketName = minioService.checkBucket();
        minioService.deleteFolder(bucketName, project.getName());

        return id;
    }

    public List<ProjectResponseDto> getUserProjects(UUID id) {
        var user = userService.getUserById(id);

        var projects = projectRepository.findAllByUser(user)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдены проекты"
                        + " для пользователя с ID = %s", user.getId())));

        return projects.stream()
                .map(mapper::projectToProjectResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String addProjectPicture(UUID id, MultipartFile[] images) {
        var project = findProjectById(id);
        var folderPath = project.getName();
        var fileUrls = minioService.uploadFile(images, folderPath);

        project.setImagesUrl(fileUrls);
        projectRepository.save(project);

        return "Изображения загружены";
    }

    @Transactional
    public String removeProjectPicture(UUID id, String imageUrl) {
        var project = findProjectById(id);

        var imageUrls = project.getImagesUrl();
        imageUrls.remove(imageUrl);
        project.setImagesUrl(imageUrls);
        projectRepository.save(project);

        minioService.deletePictureFromProject(imageUrl);
        return "Изображение удалено";
    }

    public ProjectResponseDto getProject(UUID projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        return mapper.projectToProjectResponseDto(project);
    }

    public Project findProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
    }

    private void checkProjectName(String projectName) {
        if (projectRepository.existsByName(projectName)) {
            throw new EntityNotFoundException("Project with this name " + "[" + projectName + "]" + " is existing!");
        }
    }
}
