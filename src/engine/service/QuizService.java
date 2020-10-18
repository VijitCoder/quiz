package engine.service;

import engine.dto.QuizDto;
import engine.entity.Quiz;
import engine.entity.User;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizCrudRepository;
import engine.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuizService {
    private final QuizCrudRepository quizRepo;

    private final UserCrudRepository userRepo;

    @Autowired
    public QuizService(QuizCrudRepository quizRepo, UserCrudRepository userRepo) {
        this.quizRepo = quizRepo;
        this.userRepo = userRepo;
    }

    public void storeQuiz(QuizDto dto, Principal principal) {
        User user = getUserEntity(principal.getName());
        Quiz quiz = new Quiz(dto).setAuthor(user);
        quizRepo.save(quiz);

        // TODO Грубое решение. Вообще неочевидно, что в переданном DTO появится id созданной записи.
        //  Нужно как-то сделать иначе.
        dto.setId(quiz.getId());
    }

    public void deleteQuiz(int quizId, Principal principal) {
        User user = getUserEntity(principal.getName());
        Quiz quiz = getQuizEntity(quizId);

        if (!user.equals(quiz.getAuthor())) {
            throw new AccessDeniedException("Only quiz author can delete his quiz");
        }

        quizRepo.delete(quiz);
    }

    /**
     * Get all available quizzes, without answers
     */
    public List<QuizDto> getAllQuizzes() {
        List<QuizDto> dtos = new ArrayList<>();
        // TODO как-то это можно записать через цепочку вызовов со stream и foreach или типа того.
        // Но я забыл, как именно. Нужно бы поискать и сократить весь метод.
        for (Quiz quiz: quizRepo.findAll()) {
            dtos.add(quiz.toPublicDto());
        }
        return dtos;
    }

    /**
     * Get one quiz by id
     */
    public QuizDto getQuiz(int id) {
        return getQuizEntity(id).toPublicDto();
    }

    /**
     * Check user answer for correctness to a specified quiz.
     */
    public boolean checkAnswer(int id, int[] userAnswer) {
        userAnswer = castNullableToOrderedArray(userAnswer);
        int[] correctAnswer = castNullableToOrderedArray(getQuizEntity(id).getAnswer());
        return Arrays.equals(correctAnswer, userAnswer);
    }

    private int[] castNullableToOrderedArray(int[] arr) {
        if (arr == null) {
            arr = new int[]{};
        }
        Arrays.sort(arr);
        return arr;
    }

    private Quiz getQuizEntity(int id) {
        return quizRepo
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found by id=" + id));
    }

    private User getUserEntity(String email) {
        return this.userRepo
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found by email " + email));
    }
}
