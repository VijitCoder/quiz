package engine.repository;

import engine.entity.Quiz;
import engine.entity.SolutionStatistics;
import engine.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SolutionStatRepository extends PagingAndSortingRepository<SolutionStatistics, Integer> {
    @Query(value = "SELECT stat FROM SolutionStatistics stat WHERE is_completed = true AND user = :user")
    Page<SolutionStatistics> findAllCompleted(Pageable pageable, @Param("user") User user);

    @Modifying
    @Query(value = "DELETE FROM SolutionStatistics stat WHERE quiz = :quiz")
    void deleteStatsForQuiz(@Param("quiz") Quiz quiz);

    @Modifying
    @Query(value = "DELETE FROM SolutionStatistics stat WHERE user = :user")
    void deleteStatsForUser(@Param("user") User user);
}
