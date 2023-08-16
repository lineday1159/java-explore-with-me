package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestState;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequesterId(Long userId);

    ParticipationRequest findByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestState state);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    List<ParticipationRequest> findAllByIdInAndStatus(List<Long> requestIds, RequestState state);

    Optional<ParticipationRequest> findByRequesterIdAndId(Long userId, Long requestId);

    @Modifying
    @Transactional
    @Query(value = "update requests set state = :state where id in :request_list", nativeQuery = true)
    void updateRequestsStateByIds(@Param("request_list") List<Long> requestsIds,
                                  @Param("state") String state);

    @Modifying
    @Transactional
    @Query(value = "update requests set state = :new_state " +
            "where event_id = :event_id AND state = :state", nativeQuery = true)
    void updateRequestsStateByEventAndState(@Param("event_id") Long eventId,
                                            @Param("new_state") String newState,
                                            @Param("state") String state);

}
