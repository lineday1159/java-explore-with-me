package ru.practicum.stats.service;

import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void create(StatDto statDto);

    List<ViewStats> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Long getCount(String uri);
}
