package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto newCompilationDto) throws ParseException {
        log.info("Post запрос на создание compilations - {}", newCompilationDto);
        return compilationService.save(newCompilationDto);
    }

    @DeleteMapping("admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Delete запрос на удаление compilations - {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("admin/compilations/{compId}")
    public CompilationDto patch(@PathVariable Long compId,
                                @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) throws ParseException {
        log.info("Patch запрос на изменение compilations - {}, по id - {}", updateCompilationRequest, compId);
        return compilationService.patch(compId, updateCompilationRequest);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getList(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get запрос на получение compilations");
        return compilationService.getList(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto get(@PathVariable Long compId) throws ParseException {
        log.info("Get запрос на получение compilations - {}", compId);
        return compilationService.get(compId);
    }
}
