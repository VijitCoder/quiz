package engine.controller;

import engine.dto.AnswerDto;
import engine.dto.QuizDto;
import engine.exception.EntityNotFoundException;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final QuizService service;

    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    /**
     * Add a quiz
     *
     * Note: "principal" is an authorized user in notation of the Spring Security.
     */
    @PostMapping("/quizzes")
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quiz, Principal principal) {
        service.storeQuiz(quiz, principal);
        return quiz;
    }

    /**
     * Get all available quizzes, without answers
     */
    @GetMapping("/quizzes")
    public Iterable<QuizDto> getAllQuizzes() {
        return service.getAllQuizzes();
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
        @RequestBody AnswerDto answer
    ) {
        boolean isCorrect;
        try {
            isCorrect = service.checkAnswer(id, answer.getAnswer());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
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

