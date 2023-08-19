package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto save(@PathVariable Long userId,
                           @PathVariable Long eventId,
                           @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Post запрос на добавление коммента от user - {}, по event - {}: {}", userId, eventId, newCommentDto);
        return commentService.save(newCommentDto, userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/comments/{commentId}")
    public CommentDto patch(@PathVariable Long userId,
                            @PathVariable Long commentId,
                            @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Patch запрос на обновление коммента - {} от user - {}: {}", commentId, userId, newCommentDto);
        return commentService.patch(newCommentDto, userId, commentId);
    }

    @DeleteMapping("/users/{userId}/events/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        log.info("Delete запрос на удаление коммента - {} от user - {}", commentId, userId);
        commentService.delete(userId, commentId);
    }

    @GetMapping("/events/comments/{commentId}")
    public CommentDto get(@PathVariable Long commentId) {
        log.info("Get запрос на получение коммента - {}", commentId);
        return commentService.get(commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getList(@PathVariable Long eventId,
                                    @RequestParam(required = false) List<Long> userIds,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get запрос на получение списка комментов по event - {}", eventId);
        return commentService.getList(eventId, userIds, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/comments/{commentId}")
    public CommentDto patchByAdmin(@PathVariable Long commentId,
                                   @RequestBody @Valid NewCommentDto newCommentDto) throws ParseException {
        log.info("Patch запрос на обновление комментария - {}", newCommentDto);
        return commentService.patchByAdmin(newCommentDto, commentId);
    }

    @DeleteMapping("/admin/events/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commentId) {
        log.info("Delete запрос на удаление коммента - {}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
