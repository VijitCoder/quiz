package engine.service;

import engine.entity.Quiz;
import engine.entity.User;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizCrudRepository;
import engine.repository.UserCrudRepository;
import org.springframework.stereotype.Service;

@Service
abstract public class QuizService {
    protected final QuizCrudRepository quizRepo;

    protected final UserCrudRepository userRepo;

    public QuizService(
        QuizCrudRepository quizRepo,
        UserCrudRepository userRepo
    ) {
        this.quizRepo = quizRepo;
        this.userRepo = userRepo;
    }

    protected Quiz getQuizEntity(int id) {
        return quizRepo
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found by id=" + id));
    }

    protected User getUserEntity(String email) {
        return this.userRepo
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found by email " + email));
    }
}
