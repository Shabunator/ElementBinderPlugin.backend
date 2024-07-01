package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.InnerProject;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InnerProjectRepository extends DataTablesRepository<InnerProject, UUID> {

    boolean existsByName(String innerProjectName);
}
