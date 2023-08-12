package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {
    public static Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {

        return new Compilation(null, newCompilationDto.getTitle(),
                newCompilationDto.getPinned() == null ? false : newCompilationDto.getPinned());
    }

    public static CompilationDto compilationToCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtoList) {
        return new CompilationDto(compilation.getId(), eventShortDtoList, compilation.getPinned(), compilation.getTitle());
    }
}
