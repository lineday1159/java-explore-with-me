package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto save(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto patch(NewCommentDto newCommentDto, Long userId, Long commentId);

    CommentDto patchByAdmin(NewCommentDto newCommentDto, Long commentId);

    void delete(Long userId, Long commentId);

    void deleteByAdmin(Long commentId);

    CommentDto get(Long commentId);

    List<CommentDto> getList(Long eventId, List<Long> userIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}
