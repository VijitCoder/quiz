package engine.service;

import engine.entity.Quiz;
import engine.entity.User;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizRepository;
import engine.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
abstract public class QuizService {
    protected final QuizRepository quizRepo;

    protected final UserRepository userRepo;

    public QuizService(
        QuizRepository quizRepo,
        UserRepository userRepo
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
