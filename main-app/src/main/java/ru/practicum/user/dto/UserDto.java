package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
