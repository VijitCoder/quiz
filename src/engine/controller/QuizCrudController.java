package engine.controller;

import engine.dto.QuizDto;
import engine.exception.EntityNotFoundException;
import engine.service.QuizCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController()
@RequestMapping("/api")
public class QuizCrudController {
    private static final int QUIZZES_PER_PAGE = 10;

    private final QuizCrudService service;

    @Autowired
    public QuizCrudController(QuizCrudService service) {
        this.service = service;
    }

    /**
     * Add a quiz
     * <br>
     * Note: "principal" is an authorized user in notation of the Spring Security. How to get an authorized user,
     * see <a href="https://www.baeldung.com/get-user-in-spring-security">there</a>.
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
    public Iterable<QuizDto> getAllQuizzes(@RequestParam(defaultValue = "0") Integer page) {
        Pageable paging = PageRequest.of(page, QUIZZES_PER_PAGE);
        return service.getAllQuizzes(paging);
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
}

