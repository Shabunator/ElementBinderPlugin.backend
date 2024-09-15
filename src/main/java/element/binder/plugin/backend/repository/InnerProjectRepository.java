package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.InnerProject;
import element.binder.plugin.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InnerProjectRepository extends JpaRepository<InnerProject, UUID> {

    boolean existsByName(String innerProjectName);

    Optional<List<InnerProject>> findAllByProject(Project projectId);
}
