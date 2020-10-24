package engine.controller;

import engine.dto.AnswerDto;
import engine.dto.CompletedSolutionDto;
import engine.exception.EntityNotFoundException;
import engine.service.QuizSolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;

@RestController()
@RequestMapping("/api")
public class QuizSolvingController {
    private static final int QUIZZES_PER_PAGE = 10;

    private final QuizSolvingService service;

    @Autowired
    public QuizSolvingController(QuizSolvingService service) {
        this.service = service;
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

    /**
     * Get all quizzes (paginated), completed by authorized user
     */
    @GetMapping("/quizzes/completed")
    public Iterable<CompletedSolutionDto> getCompletedSolutions(
        @RequestParam(defaultValue = "0") Integer page,
        Principal principal
    ) {
        // Sorting here - that is bad. But it's all because in the Spring messed up pagination (which is directly
        // relates to representation layer but not to DB) and sorting by entity fields (which is directly related
        // to DB, but can by weakly related to representation layer).
        // And there is no normal way to setup sorting later, deeper in the app layers.
        Pageable paging = PageRequest.of(page, QUIZZES_PER_PAGE, Sort.by("createdAt").descending());

        return service.getCompletedSolutions(paging, principal);
    }
}
