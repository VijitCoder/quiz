package engine.repository;

import engine.entity.Quiz;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuizCrudRepository extends PagingAndSortingRepository<Quiz, Integer> {
}


