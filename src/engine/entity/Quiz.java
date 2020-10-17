package engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Integer id;

    @NotEmpty
    @JsonProperty
    private String title;

    @NotEmpty
    @JsonProperty
    private String text;

    @Size(min = 2)
    @JsonProperty
    private String[] options;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int[] answer;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public int[] getAnswer() {
        return answer;
    }
}