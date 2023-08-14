package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> eventList) {

        return new Compilation(null, newCompilationDto.getTitle(),
                newCompilationDto.getPinned() != null && newCompilationDto.getPinned(), eventList);
    }

    public static CompilationDto compilationToCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (compilation.getEvents() != null) {
            eventShortDtoList = compilation.getEvents().stream()
                    .map(event -> {
                        try {
                            return EventMapper.eventToEventShortDto(event);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
        }
        return new CompilationDto(compilation.getId(), eventShortDtoList, compilation.getPinned(), compilation.getTitle());
    }
}
