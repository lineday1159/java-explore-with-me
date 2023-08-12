package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    @Autowired
    private final CompilationRepository compilationRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final StatClient statClient;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.save(CompilationMapper.newCompilationDtoToCompilation(newCompilationDto));
        CompilationDto compilationDto = CompilationMapper.compilationToCompilationDto(compilation, new ArrayList<>());
        if (newCompilationDto.getEvents() != null) {
            if (newCompilationDto.getEvents().size() != 0) {
                compilationRepository.saveCompilationEvents(compilation.getId(), newCompilationDto.getEvents());
                compilationDto.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents())
                        .stream().map(event -> {
                            try {
                                Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                                return EventMapper.eventToEventShortDto(event, views);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList()));
            }
        }
        return compilationDto;
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        "Compilation with id=" + compId + " was not found",
                        "The required object was not found."));
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patch(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found",
                        "The required object was not found."));
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = CompilationMapper.compilationToCompilationDto(compilation, new ArrayList<>());
        List<Event> eventList = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            compilationRepository.deleteCompilationEventsExcludingEventIds(compId, updateCompilationRequest.getEvents());
            compilationRepository.updateCompilationEvents(compId, updateCompilationRequest.getEvents());
            if (updateCompilationRequest.getEvents().size() != 0) {
                eventList = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());

            }
        } else {
            eventList = eventRepository.findAllByCompilationId(compId);
        }
        compilationDto.setEvents(eventList.stream().map(event -> {
            try {
                Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                return EventMapper.eventToEventShortDto(event, views);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));

        return compilationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getList(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        List<CompilationDto> compilationDtoList = compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilation -> CompilationMapper.compilationToCompilationDto(compilation, new ArrayList<>()))
                .collect(Collectors.toList());

        for (CompilationDto compilationDto : compilationDtoList) {
            compilationDto.setEvents(eventRepository.findAllByCompilationId(compilationDto.getId()).stream()
                    .map(event -> {
                        try {
                            Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                            return EventMapper.eventToEventShortDto(event, views);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()));
        }
        return compilationDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found",
                        "The required object was not found."));
        CompilationDto compilationDto = CompilationMapper.compilationToCompilationDto(compilation, new ArrayList<>());
        List<EventShortDto> eventShortDtoList = eventRepository.findAllByCompilationId(compId).stream().map(event -> {
            try {
                Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                return EventMapper.eventToEventShortDto(event, views);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        compilationDto.setEvents(eventShortDtoList);

        return compilationDto;
    }
}
