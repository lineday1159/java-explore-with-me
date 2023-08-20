package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventController {
    private final StatClient statClient;
    private final EventService eventService;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("/events/{eventId}")
    public EventFullDto getByPublic(@PathVariable long eventId,
                                    HttpServletRequest request) throws ParseException {
        log.info("Get запрос на получение event - {}", eventId);
        statClient.postStats(new StatDto("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()));
        return eventService.getByPublic(eventId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getListByPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size,
                                               HttpServletRequest request
    ) throws ParseException {
        log.info("Get запрос на получение списка event");
        statClient.postStats(new StatDto("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()));
        return eventService.getListByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getListByAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size
    ) throws ParseException {
        log.info("Get запрос админа на получение event");
        return eventService.getListByAdmin(users, states, categories, rangeEnd, rangeStart, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto patchByAdmin(@PathVariable long eventId,
                                     @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) throws ParseException {
        log.info("Patch запрос на обновление event - {}", updateEventAdminRequest);
        return eventService.patchByAdmin(eventId, updateEventAdminRequest);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto save(@PathVariable long userId,
                             @Valid @RequestBody NewEventDto newEventDto) throws ParseException {
        log.info("Post запрос на создание event - {}", newEventDto);
        return eventService.save(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto get(@PathVariable long userId,
                            @PathVariable long eventId) throws ParseException {
        log.info("Get запрос на получение event - {}", eventId);
        return eventService.get(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto patch(@PathVariable long userId,
                              @PathVariable long eventId,
                              @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) throws ParseException {
        log.info("Patch запрос на обновление event - {}", updateEventUserRequest);
        return eventService.patch(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getList(@PathVariable long userId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(defaultValue = "10") @Positive int size) throws ParseException {
        log.info("Get запрос на получение списка event по userid - {}", userId);
        return eventService.getList(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsList(@PathVariable long userId,
                                                         @PathVariable long eventId) throws ParseException {
        log.info("Get запрос на получение списка request по eventid - {}", eventId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequest(@PathVariable long userId,
                                                       @PathVariable long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest requestUpdate) throws ParseException {
        log.info("patch запрос на изменение request по eventid - {} у userId - {}", eventId, userId);
        return eventService.patchRequest(userId, eventId, requestUpdate);
    }

}
