package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    @Autowired
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable long userId) throws ParseException {
        log.info("Get запрос на получение requests - {}", userId);
        return requestService.get(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@PathVariable long userId,
                                        @RequestParam long eventId) throws ParseException {
        log.info("Post запрос на фиксацию request - {}, пользователем - {}", eventId, userId);
        return requestService.save(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto chanel(@PathVariable long userId,
                                          @PathVariable long requestId) throws ParseException {
        log.info("Patch запрос на изменение request - {}, пользователем - {}", requestId, userId);
        return requestService.cancel(userId, requestId);
    }
}
