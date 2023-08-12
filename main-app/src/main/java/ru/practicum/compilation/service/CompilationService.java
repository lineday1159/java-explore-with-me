package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto patch(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getList(Boolean pinned, int from, int size);

    CompilationDto get(Long compId);
}
