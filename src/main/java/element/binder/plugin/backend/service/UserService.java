package element.binder.plugin.backend.service;

import element.binder.plugin.backend.entity.User;
import element.binder.plugin.backend.exception.EntityNotFoundException;
import element.binder.plugin.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Найти пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %s не найден", id)));
    }

    /**
     * Сохранить пользователя
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь
     */
    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }

        User savedUser = userRepository.save(user);

        log.debug("Пользователь с ID = {} был сохранен", savedUser.getId());
        return savedUser;
    }
}
