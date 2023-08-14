package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByInitiatorId(Long userID, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    @Query(value = "Select e.* " +
            "From Events e " +
            "Join compilation_event c on c.event_id = e.id " +
            "where c.compilation_id = :compilation_id", nativeQuery = true)
    List<Event> findAllByCompilationId(@Param("compilation_id") Long compilationId);

    List<Event> findAllByCategoryId(Long categoryId);

    @Query("Select e " +
            "From Event e " +
            "Where (:users is null or e.initiator.id in :users) and " +
            "(:states is null or e.state in :states) and " +
            "(:categories is null or e.category.id in :categories) and " +
            "(cast(:rangeStart as date) is null or cast(:rangeStart as date) < e.eventDate) and " +
            "(cast(:rangeEnd as date) is null or cast(:rangeEnd as date) > e.eventDate)")
    Page<Event> searchByAdmin(@Param("users") Optional<List<Long>> users,
                              @Param("states") Optional<List<EventState>> states,
                              @Param("categories") Optional<List<Long>> categories,
                              @Param("rangeStart") Optional<Date> rangeStart,
                              @Param("rangeEnd") Optional<Date> rangeEnd,
                              Pageable pageable);

    @Query("Select e " +
            "From Event e " +
            "Where (:text is null or e.annotation like %:text% or e.description like %:text%) and " +
            "(:categories is null or e.category.id in :categories) and " +
            "(:paid is null or e.paid = :paid) and " +
            "(cast(:rangeStart as date) is null or cast(:rangeStart as date) < e.eventDate) and " +
            "(cast(:rangeEnd as date) is null or cast(:rangeEnd as date) > e.eventDate)")
    Page<Event> searchByPublic(@Param("text") Optional<String> text,
                               @Param("categories") Optional<List<Long>> categories,
                               @Param("paid") Optional<Boolean> paid,
                               @Param("rangeStart") Optional<Date> rangeStart,
                               @Param("rangeEnd") Optional<Date> rangeEnd,
                               Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update events set confirmed_requests = (confirmed_requests + :new_confirmed_requests) " +
            "where id = :event_id", nativeQuery = true)
    void updateConfirmedRequestsById(@Param("event_id") Long eventId,
                                     @Param("new_confirmed_requests") int confirmedRequests);
}
