package engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * DTO: slice of {@link engine.entity.SolutionStatistics}, only successfully passed quizzes
 */
public class CompletedSolutionDto {
    /**
     * Quiz id
     */
    @JsonProperty
    private int id;

    /**
     * Date/time when the quiz was completed by an authorized user
     */
    @JsonProperty
    private Date completedAt;

    public CompletedSolutionDto setId(int id) {
        this.id = id;
        return this;
    }

    public CompletedSolutionDto setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
        return this;
    }
}
