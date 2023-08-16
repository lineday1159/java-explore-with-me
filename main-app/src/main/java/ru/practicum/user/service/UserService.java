package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getList(List<Long> ids, int from, int size);

    UserDto save(UserDto userDto);

    void delete(Long id);
}
