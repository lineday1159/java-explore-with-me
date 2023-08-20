package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query(value = "select s.app as app, s.uri as uri, count(s.id) as hits " +
            "from statistics s " +
            "where s.timestamp > cast(:startDate as date) and s.timestamp < cast(:endDate as date) " +
            "and (s.uri in :uris or :uris is null) " +
            "group by s.app, s.uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> search(@Param("startDate") LocalDateTime start,
                           @Param("endDate") LocalDateTime end,
                           @Param("uris") List<String> uris);

    @Query(value = "select s.app as app, s.uri as uri, count(*) as hits " +
            "from (Select distinct s.app, s.uri from statistics s " +
            "where s.timestamp > cast(:startDate as date) and s.timestamp < cast(:endDate as date) " +
            "and (s.uri in :uris or :uris is null)) as s " +
            "group by s.app, s.uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> searchUnique(@Param("startDate") LocalDateTime start,
                                 @Param("endDate") LocalDateTime end,
                                 @Param("uris") List<String> uris);

    @Query(value = "select count(*) " +
            "from (Select distinct s.ip from statistics s " +
            "where s.uri = :uri) as st ", nativeQuery = true)
    Long getCount(@Param("uri") String uri);
}