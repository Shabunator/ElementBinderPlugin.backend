package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.Project;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends DataTablesRepository<Project, UUID> {
}
