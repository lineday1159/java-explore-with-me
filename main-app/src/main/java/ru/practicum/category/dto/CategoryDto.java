package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
