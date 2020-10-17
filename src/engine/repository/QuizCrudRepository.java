package engine.repository;

import engine.entity.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizCrudRepository extends CrudRepository<Quiz, Integer> {
}


