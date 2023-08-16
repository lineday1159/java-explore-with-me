package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    public static Category categoryDtoToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> categoriesToCategoriesDto(Iterable<Category> categories) {
        List<CategoryDto> categoriesDtos = new ArrayList<>();
        for (Category category : categories) {
            categoriesDtos.add(categoryToCategoryDto(category));
        }
        return categoriesDtos;
    }
}
