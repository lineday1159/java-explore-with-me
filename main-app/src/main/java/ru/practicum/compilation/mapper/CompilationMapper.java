package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompilationMapper {
    public static Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> eventList) {
        return new Compilation(null, newCompilationDto.getTitle(),
                newCompilationDto.getPinned() != null && newCompilationDto.getPinned(), eventList);
    }

    public static CompilationDto compilationToCompilationDto(Compilation compilation, HashMap<Long, Long> viewsMap) throws ParseException {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (compilation.getEvents() != null) {
            eventShortDtoList = EventMapper.eventsToEventsShortDto(compilation.getEvents(), viewsMap);
        }
        return new CompilationDto(compilation.getId(), eventShortDtoList, compilation.getPinned(), compilation.getTitle());
    }
}
