package engine.service;

import engine.entity.Quiz;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class QuizService {
    private final QuizCrudRepository repo;

    @Autowired
    public QuizService(QuizCrudRepository repo) {
        this.repo = repo;
    }

    public void storeQuiz(Quiz quiz) {
        repo.save(quiz);
    }

    /**
     * Get all available quizzes, without answers
     */
    public Iterable<Quiz> getAllQuizzes() {
        return repo.findAll();
    }

    /**
     * Get one quiz by id
     */
    public Quiz getQuiz(int id) {
        return repo
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
