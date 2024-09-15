package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.Project;
import element.binder.plugin.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    boolean existsByName(String projectName);

    Optional<List<Project>> findAllByUser(User user);
}
