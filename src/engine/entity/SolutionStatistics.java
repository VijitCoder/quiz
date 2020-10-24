package engine.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="solution_statistics")
public class SolutionStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name="created_at", updatable = false)
    private Date createdAt;

    public Quiz getQuiz() {
        return quiz;
    }

    public SolutionStatistics setQuiz(Quiz quiz) {
        this.quiz = quiz;
        return this;
    }

    public User getUser() {
        return user;
    }

    public SolutionStatistics setUser(User user) {
        this.user = user;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public SolutionStatistics setCompleted(boolean completed) {
        isCompleted = completed;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public SolutionStatistics setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
