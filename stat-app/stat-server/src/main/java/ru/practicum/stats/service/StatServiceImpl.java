package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.StatMapper;
import ru.practicum.stats.repository.StatRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void create(StatDto statDto) {
        statRepository.save(StatMapper.statDtoToStat(statDto, null, new Date()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> get(Date start, Date end, List<String> uris, Boolean unique) {
        if (end.before(start)) {
            throw new RuntimeException("start date must be before end date");
        }
        if (unique) {
            return statRepository.searchUnique(start, end,
                    uris == null ? new ArrayList<>() : uris);
        } else {
            return statRepository.search(start, end,
                    uris == null ? new ArrayList<>() : uris);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCount(String uri) {
        return statRepository.getCount(uri);
    }
}
