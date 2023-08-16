package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.service.StatService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid StatDto statDto) {
        log.info("Post запрос на создание статистики -{}", statDto);
        statService.create(statDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end,
                               @RequestParam(required = false) List<String> uris,
                               @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get запрос на получение кол-во запросов по -{}", uris);
        return statService.get(start, end, uris, unique);
    }

    @GetMapping("/statsCount")
    public Long getCount(@RequestParam(name = "eventUri") String eventUri) {
        log.info("Get запрос на получение кол-во уникальных запросов по -{}", eventUri);
        return statService.getCount(eventUri);
    }
}
