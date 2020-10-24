package engine.service;

import engine.dto.CompletedSolutionDto;
import engine.entity.Quiz;
import engine.entity.SolutionStatistics;
import engine.entity.User;
import engine.repository.QuizRepository;
import engine.repository.SolutionStatRepository;
import engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class serving for quizzes solving only.
 */
@Service
public class QuizSolvingService extends QuizService {
    private final SolutionStatRepository solutionStatRepo;

    @Autowired
    public QuizSolvingService(
        QuizRepository quizRepo,
        UserRepository userRepo,
        SolutionStatRepository solutionStatRepo
    ) {
        super(quizRepo, userRepo);
        this.solutionStatRepo = solutionStatRepo;
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
}
