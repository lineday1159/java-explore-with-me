package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        List<Event> eventList = newCompilationDto.getEvents() == null ?
                new ArrayList<>() : eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(CompilationMapper
                .newCompilationDtoToCompilation(newCompilationDto, eventList));

        return CompilationMapper.compilationToCompilationDto(compilation);
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
        List<Event> eventList = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            eventList = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(eventList);
        }
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = CompilationMapper.compilationToCompilationDto(compilation);

        return compilationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getList(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        List<CompilationDto> compilationDtoList = compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilation -> CompilationMapper.compilationToCompilationDto(compilation))
                .collect(Collectors.toList());

        return compilationDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found",
                        "The required object was not found."));
        CompilationDto compilationDto = CompilationMapper.compilationToCompilationDto(compilation);

        return compilationDto;
    }
}
