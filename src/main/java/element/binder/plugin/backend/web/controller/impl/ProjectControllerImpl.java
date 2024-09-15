package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.service.InnerProjectService;
import element.binder.plugin.backend.service.ProjectService;
import element.binder.plugin.backend.web.controller.ProjectController;
import element.binder.plugin.backend.web.model.request.InnerProjectRequestDto;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.InnerProjectResponseDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectControllerImpl implements ProjectController {

    private final ProjectService projectService;
    private final InnerProjectService innerProjectService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ProjectResponseDto> updateProject(@PathVariable("id") UUID id,
                                                            @RequestBody @Valid ProjectRequestDto request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<UUID> deleteProject(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(projectService.delete(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ProjectResponseDto> getProject(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PostMapping("/{id}/inner-project")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<InnerProjectResponseDto> createInnerProject(@PathVariable UUID id,
                                                                      @RequestBody @Valid InnerProjectRequestDto request) {
        return ResponseEntity.ok(innerProjectService.create(id, request));
    }

    @GetMapping("/{id}/inner-project")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<InnerProjectResponseDto>> getAllInnerProjectsByProject(@PathVariable UUID id) {
        return ResponseEntity.ok(innerProjectService.getAllByProjectId(id));
    }
}
