package ru.practicum.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into compilation_event (event_id,compilation_id) " +
            "Select id, :compilation_id " +
            "From events where id in :events_list", nativeQuery = true)
    void saveCompilationEvents(@Param("compilation_id") Long compilationId,
                               @Param("events_list") List<Long> eventsId);

    @Modifying
    @Transactional
    @Query(value = "delete from compilation_event where event_id not in :events_list " +
            "and compilation_id = :compilation_id", nativeQuery = true)
    void deleteCompilationEventsExcludingEventIds(@Param("compilation_id") Long compilationId,
                                                  @Param("events_list") List<Long> eventsId);

    @Modifying
    @Transactional
    @Query(value = "insert into compilation_event (event_id,compilation_id) " +
            "Select id, :compilation_id " +
            "From events where id in :events_list " +
            "and id not in (select c.event_id from compilation_event c where c.compilation_id =:compilation_id)", nativeQuery = true)
    void updateCompilationEvents(@Param("compilation_id") Long compilationId,
                                 @Param("events_list") List<Long> eventsId);

    Page<Compilation> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);


}
