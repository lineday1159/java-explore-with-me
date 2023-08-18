package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface EventService {
    EventFullDto save(Long userId, NewEventDto newEventDto) throws ParseException;

    EventFullDto patch(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) throws ParseException;

    EventFullDto patchByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) throws ParseException;

    EventFullDto get(Long userId, Long eventId) throws ParseException;

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId) throws ParseException;

    EventRequestStatusUpdateResult patchRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate) throws ParseException;

    EventFullDto getByPublic(Long eventId) throws ParseException;

    List<EventFullDto> getListByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                      LocalDateTime rangeEnd, LocalDateTime rangeStart, int from, int size) throws ParseException;

    List<EventShortDto> getList(Long userId, int from, int size) throws ParseException;

    List<EventShortDto> getListByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, int from, int size) throws ParseException;

    HashMap<Long, Long> getViewsByEvents(List<Event> events) throws ParseException;
}
