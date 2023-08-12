package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.ConflictException;
import ru.practicum.error.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public CategoryDto get(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Category with id=" + id + " was not found",
                        "The required object was not found."));
        return CategoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getList(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return CategoryMapper.categoriesToCategoriesDto(categoryRepository.findAll(pageable));
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = CategoryMapper.categoryDtoToCategory(categoryDto);
        try {
            return CategoryMapper.categoryToCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getCause().getMessage(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Category with id=" + id + " was not found",
                        "The required object was not found."));
        category.setName(categoryDto.getName());
        try {
            categoryRepository.save(category);
            return CategoryMapper.categoryToCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getCause().getMessage(), e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Category with id=" + id + " was not found",
                        "The required object was not found."));
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getCause().getMessage(), e.getMessage());
        }
    }
}
