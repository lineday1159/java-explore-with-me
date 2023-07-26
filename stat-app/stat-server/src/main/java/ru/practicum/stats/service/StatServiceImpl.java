package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.StatMapper;
import ru.practicum.stats.repository.StatRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    @Autowired
    StatRepository statRepository;

    @Override
    public void create(StatDto statDto) {
        statRepository.save(StatMapper.statDtoToStat(statDto, null, new Date()));
    }

    @Override
    public List<ViewStats> get(Date start, Date end, List<String> uris, Boolean unique) {

        if (unique) {
            return statRepository.searchUnique(start, end, uris == null ? new ArrayList<String>() : uris);
        } else {
            return statRepository.search(start, end, uris == null ? new ArrayList<String>() : uris);
        }
    }
}
