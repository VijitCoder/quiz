package engine.service;

import engine.entity.Quiz;
import engine.entity.User;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizCrudRepository;
import engine.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;

@Service
public class QuizService {
    private final QuizCrudRepository quizRepo;

    private final UserCrudRepository userRepo;

    @Autowired
    public QuizService(QuizCrudRepository quizRepo, UserCrudRepository userRepo) {
        this.quizRepo = quizRepo;
        this.userRepo = userRepo;
    }

    public void storeQuiz(Quiz quiz, Principal principal) {
        User user = this.userRepo
            .findByEmail(principal.getName())
            .orElseThrow(() -> new EntityNotFoundException("User not found in the DB"));
        quiz.setAuthor(user);
        quizRepo.save(quiz);
    }

    /**
     * Get all available quizzes, without answers
     */
    public Iterable<Quiz> getAllQuizzes() {
        return quizRepo.findAll();
    }

    /**
     * Get one quiz by id
     */
    public Quiz getQuiz(int id) {
        return quizRepo
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
    }

    /**
     * Check user answer for correctness to a specified quiz.
     */
    public boolean checkAnswer(int id, int[] userAnswer) {
        userAnswer = castNullableToOrderedArray(userAnswer);
        int[] correctAnswer = castNullableToOrderedArray(getQuiz(id).getAnswer());
        return Arrays.equals(correctAnswer, userAnswer);
    }

    private int[] castNullableToOrderedArray(int[] arr) {
        if (arr == null) {
            arr = new int[]{};
        }
        Arrays.sort(arr);
        return arr;
    }
}
