package engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class QuizDto {
    @JsonProperty
    private Integer id;

    @NotEmpty
    @JsonProperty
    private String title;

    @NotEmpty
    @JsonProperty
    private String text;

    @NotEmpty
    @Size(min = 2, message="Need at least 2 options")
    @JsonProperty
    private String[] options;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int[] answer;

    public QuizDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QuizDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public QuizDto setText(String text) {
        this.text = text;
        return this;
    }

    public String[] getOptions() {
        return options;
    }

    public QuizDto setOptions(String[] options) {
        this.options = options;
        return this;
    }

    public int[] getAnswer() {
        return answer;
    }
}
