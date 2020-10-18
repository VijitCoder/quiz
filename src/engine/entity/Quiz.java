package engine.entity;

import engine.dto.QuizDto;

import javax.persistence.*;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private String text;

    private String[] options;

    private int[] answer;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public Quiz() {
    }

    public Quiz(QuizDto data) {
        setTitle(data.getTitle());
        setText(data.getText());
        setOptions(data.getOptions());
        setAnswer(data.getAnswer());
    }

    /**
     * Prepare public representation of the Quiz entity
     */
    public QuizDto toPublicDto() {
        return new QuizDto()
            .setId(getId())
            .setTitle(getTitle())
            .setText(getText())
            .setOptions(getOptions());
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Quiz setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Quiz setText(String text) {
        this.text = text;
        return this;
    }

    public String[] getOptions() {
        return options;
    }

    public Quiz setOptions(String[] options) {
        this.options = options;
        return this;
    }

    public int[] getAnswer() {
        return answer;
    }

    public Quiz setAnswer(int[] answer) {
        this.answer = answer;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public Quiz setAuthor(User author) {
        this.author = author;
        return this;
    }
}
