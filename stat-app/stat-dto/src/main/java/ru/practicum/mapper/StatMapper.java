package ru.practicum.mapper;

import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatMapper {
    public static Stat statDtoToStat(StatDto statDto, Long id, Date datetime) {
        return new Stat(id, statDto.getApp(), statDto.getUri(), statDto.getIp(), datetime);
    }

    public static StatDto statToStatDto(Stat stat) {
        return new StatDto(stat.getApp(), stat.getUri(), stat.getIp());
    }

    public static List<StatDto> statsToStatsDto(Iterable<Stat> stats) {
        List<StatDto> statsDto = new ArrayList<>();
        for (Stat stat : stats) {
            statsDto.add(statToStatDto(stat));
        }
        return statsDto;
    }
}
