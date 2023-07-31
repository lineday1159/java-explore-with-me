package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Stat;

import java.util.Date;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query(value = "select s.app as app, s.uri as uri, count(s.id) as hits " +
            "from statistics s " +
            "where s.timestamp > ?1 and s.timestamp < ?2 " +
            "and ( s.uri in (?3) or ?3 is null) " +
            "group by s.app, s.uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> search(Date start, Date end, List<String> uris);

    @Query(value = "select s.app as app, s.uri as uri, count(*) as hits " +
            "from (Select distinct s.app, s.uri from statistics s " +
            "where s.timestamp > ?1 and s.timestamp < ?2 " +
            "and ( s.uri in (?3) or ?3 is null)) as s " +
            "group by s.app, s.uri " +
            "order by hits desc", nativeQuery = true)
    List<ViewStats> searchUnique(Date start, Date end, List<String> uris);

}
