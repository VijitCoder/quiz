package engine.controller;

import engine.dto.AnswerDto;
import engine.dto.CompletedSolutionDto;
import engine.dto.QuizDto;
import engine.exception.EntityNotFoundException;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;

/**
 * How to get an authorized user, see <a href="https://www.baeldung.com/get-user-in-spring-security">there</a>.
 */
@RestController()
@RequestMapping("/api")
public class QuizController {
    private static final int QUIZZES_PER_PAGE = 10;

    private final QuizService service;

    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    /**
     * Add a quiz
     * <br>
     * Note: "principal" is an authorized user in notation of the Spring Security.
     */
    @PostMapping("/quizzes")
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quiz, Principal principal) {
        service.storeQuiz(quiz, principal);
        return quiz;
    }

    /**
     * Get all available quizzes (paginated), without answers
     * <br>
     * Note: according the task we support only page num from the user request. Therefore the page size is hardcoded.
     */
    @GetMapping("/quizzes")
    public Iterable<QuizDto> getAllQuizzes(@RequestParam(defaultValue = "0") Integer pageNo) {
        Pageable paging = PageRequest.of(pageNo, QUIZZES_PER_PAGE);
        return service.getAllQuizzes(paging);
    }

    /**
     * Get all quizzes (paginated), completed by authorized user
     */
    @GetMapping("/quizzes/completed")
    public Iterable<CompletedSolutionDto> getCompletedSolutions(
        @RequestParam(defaultValue = "0") Integer pageNo,
        Principal principal
    ) {
        // Sorting here - that is bad. But it's all because in the Spring messed up pagination (which is directly
        // relates to representation layer but not to DB) and sorting by entity fields (which is directly related
        // to DB, but can by weakly related to representation layer).
        // And there is no normal way to setup sorting later, deeper in the app layers.
        Pageable paging = PageRequest.of(pageNo, QUIZZES_PER_PAGE, Sort.by("createdAt").descending());

        return service.getCompletedSolutions(paging, principal);
    }

    /**
     * Get one quiz by id
     */
    @GetMapping("/quizzes/{id}")
    public QuizDto getQuiz(@PathVariable int id) {
        try {
            return service.getQuiz(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Delete the quiz. Only its author can do that.
     */
    @DeleteMapping("/quizzes/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable int id, Principal principal) {
        try {
            service.deleteQuiz(id, principal);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Accept the answer for a specified quiz and check it for correctness.
     */
    @PostMapping("/quizzes/{id}/solve")
    public HashMap<String, Object> checkAnswer(
        @PathVariable int id,
        @RequestBody AnswerDto answer,
        Principal principal
    ) {
        boolean isCorrect;
        try {
            isCorrect = service.checkAnswer(id, answer.getAnswer(), principal);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        HashMap<String, Object> result = new HashMap<>();
        if (isCorrect) {
            result.put("success", true);
            result.put("feedback", "Congratulations, you're right!");
        } else {
            result.put("success", false);
            result.put("feedback", "Wrong answer! Please, try again.");
        }

        return result;
    }
}

