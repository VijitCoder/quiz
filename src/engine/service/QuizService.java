package engine.service;

import engine.dto.CompletedSolutionDto;
import engine.dto.QuizDto;
import engine.entity.Quiz;
import engine.entity.SolutionStatistics;
import engine.entity.User;
import engine.exception.EntityNotFoundException;
import engine.repository.QuizCrudRepository;
import engine.repository.SolutionStatCrudRepository;
import engine.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class QuizService {
    private final QuizCrudRepository quizRepo;

    private final UserCrudRepository userRepo;

    private final SolutionStatCrudRepository solutionStatRepo;

    @Autowired
    public QuizService(
        QuizCrudRepository quizRepo,
        UserCrudRepository userRepo,
        SolutionStatCrudRepository solutionStatRepo
    ) {
        this.quizRepo = quizRepo;
        this.userRepo = userRepo;
        this.solutionStatRepo = solutionStatRepo;
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
     * Get all available quizzes (paginated), without answers
     * <p>
     * The paged result contains Quizzes as entities, but we need these quizzes in the DTO representation.
     * That's why we need to replace paged content with DTOs.
     */
    public Page<QuizDto> getAllQuizzes(Pageable paging) {
        Page<Quiz> quizzesPage = quizRepo.findAll(paging);
        List<QuizDto> dtos = new ArrayList<>();

        if (quizzesPage.hasContent()) {
            // TODO как-то это можно записать через цепочку вызовов со stream и foreach или типа того.
            // Но я забыл, как именно. Нужно бы поискать и сократить весь метод.
            for (Quiz quiz : quizzesPage.getContent()) {
                dtos.add(quiz.toPublicDto());
            }
        }

        return new PageImpl<>(dtos, quizzesPage.getPageable(), quizzesPage.getTotalElements());
    }

    /**
     * Get all quizzes (paginated), completed by authorized user
     */
    public Page<CompletedSolutionDto> getCompletedSolutions(Pageable paging, Principal principal) {
        User user = getUserEntity(principal.getName());
        Page<SolutionStatistics> completedSolutionsPage = solutionStatRepo.findAllCompleted(paging, user);
        List<CompletedSolutionDto> dtos = new ArrayList<>();

        if (completedSolutionsPage.hasContent()) {
            for (SolutionStatistics stat : completedSolutionsPage.getContent()) {
                dtos.add(
                    new CompletedSolutionDto()
                    .setId(stat.getQuiz().getId())
                    .setCompletedAt(stat.getCreatedAt())
                );
            }
        }

        return new PageImpl<>(dtos, completedSolutionsPage.getPageable(), completedSolutionsPage.getTotalElements());
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
    public boolean checkAnswer(int quizId, int[] userAnswer, Principal principal) {
        User user = getUserEntity(principal.getName());
        Quiz quiz = getQuizEntity(quizId);

        userAnswer = castNullableToOrderedArray(userAnswer);
        int[] correctAnswer = castNullableToOrderedArray(quiz.getAnswer());
        boolean isCorrect = Arrays.equals(correctAnswer, userAnswer);

        solutionStatRepo.save(
            new SolutionStatistics()
                .setQuiz(quiz)
                .setUser(user)
                .setCompleted(isCorrect)
                // Note: Spring has a way to set a date automatically, but it's too complicated for such simple job.
                .setCreatedAt(new Date())
        );

        return isCorrect;
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
