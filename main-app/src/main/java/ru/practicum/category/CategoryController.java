package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories/{id}")
    public CategoryDto get(@PathVariable long id) {
        return categoryService.get(id);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getList(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get запрос на получение списка category");
        return categoryService.getList(from, size);
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Post запрос на создание category - {}", categoryDto);
        return categoryService.save(categoryDto);
    }

    @PatchMapping("/admin/categories/{id}")
    public CategoryDto update(@PathVariable long id,
                              @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Patch запрос на обновление category - {}, по id - {}", categoryDto, id);
        return categoryService.update(id, categoryDto);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("Delete запрос на удаление categories - {}", id);
        categoryService.delete(id);
    }
}
