package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.Date;
import java.util.List;

public interface CommentService {

    public CommentDto save(NewCommentDto newCommentDto, Long userId, Long eventId);

    public CommentDto update(UpdateCommentDto updateCommentDto, Long userId, Long commentId);

    public void delete(Long userId, Long commentId);

    public CommentDto get(Long commentId);

    public List<CommentDto> getList(Long eventId, List<Long> userIds, Date rangeStart, Date rangeEnd, int from, int size);
}
