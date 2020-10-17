package engine.repository;

import engine.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
}
