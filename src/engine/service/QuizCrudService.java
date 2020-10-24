package engine.service;

import engine.dto.QuizDto;
import engine.entity.Quiz;
import engine.entity.User;
import engine.repository.QuizCrudRepository;
import engine.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for the quiz's CRUD operations only.
 */
@Service
public class QuizCrudService extends QuizService {
    @Autowired
    public QuizCrudService(
        QuizCrudRepository quizRepo,
        UserCrudRepository userRepo
    ) {
        super(quizRepo, userRepo);
    }

    public void storeQuiz(QuizDto dto, Principal principal) {
        User user = getUserEntity(principal.getName());
        Quiz quiz = new Quiz(dto).setAuthor(user);
        quizRepo.save(quiz);

        // TODO Грубое решение. Вообще неочевидно, что в переданном DTO появится id созданной записи.
        //  Нужно как-то сделать иначе.
        dto.setId(quiz.getId());
    }

    /**
     * Get one quiz by id
     */
    public QuizDto getQuiz(int id) {
        return getQuizEntity(id).toPublicDto();
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
}
