package element.binder.plugin.backend.web.controller.impl;

import element.binder.plugin.backend.service.ProjectService;
import element.binder.plugin.backend.web.controller.UserController;
import element.binder.plugin.backend.web.model.request.ProjectRequestDto;
import element.binder.plugin.backend.web.model.response.ProjectResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/user")
public class UserControllerImpl implements UserController {

    private final ProjectService projectService;

    @PostMapping("/{id}/projects")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ProjectResponseDto> createProject(@PathVariable("id") UUID id,
                                                            @RequestBody @Valid ProjectRequestDto request) {
        return ResponseEntity.ok(projectService.create(id, request));
    }

    @GetMapping("/{id}/projects")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(projectService.getUserProjects(id));
    }
}
