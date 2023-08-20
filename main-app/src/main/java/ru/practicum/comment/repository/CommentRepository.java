package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("Select c " +
            "From Comment c " +
            "Where (:users is null or c.user.id in :users) and " +
            "(c.event.id = :event) and " +
            "(cast(:rangeStart as date) is null or cast(:rangeStart as date) < c.createdOn) and " +
            "(cast(:rangeEnd as date) is null or cast(:rangeEnd as date) > c.createdOn)")
    Page<Comment> search(@Param("event") Long eventId,
                         @Param("users") Optional<List<Long>> users,
                         @Param("rangeStart") Optional<LocalDateTime> rangeStart,
                         @Param("rangeEnd") Optional<LocalDateTime> rangeEnd,
                         Pageable pageable);

}
