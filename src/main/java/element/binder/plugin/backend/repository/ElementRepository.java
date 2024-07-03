package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.Element;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ElementRepository extends JpaRepository<Element, UUID> {

}
