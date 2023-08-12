package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.text.ParseException;
import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> get(Long userId) throws ParseException;

    ParticipationRequestDto save(Long userId, Long eventId) throws ParseException;

    ParticipationRequestDto cancel(Long userId, Long requestId) throws ParseException;
}
