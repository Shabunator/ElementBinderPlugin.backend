package element.binder.plugin.backend.repository;

import element.binder.plugin.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Boolean existsByName(String name);

    Boolean existsByEmail(String email);

    Optional<User> findById(UUID userId);
}
