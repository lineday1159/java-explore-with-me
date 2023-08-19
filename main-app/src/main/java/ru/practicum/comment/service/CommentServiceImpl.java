package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.error.ConflictException;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto save(NewCommentDto newCommentDto, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=" + eventId + " was not found", "The required object was not found."));

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                    "Event with id=" + eventId + " must be published", "The comment can not be published.");
        }

        Comment comment = commentRepository.save(CommentMapper.newCommentDtoToComment(newCommentDto, user, event));
        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto patch(NewCommentDto newCommentDto, Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found",
                    "The required object was not found.");
        }
        if (comment.getCreatedOn().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ConflictException(
                    "Comment with id=" + commentId + " was created more than 1 hour ago", "The comment can not be edited.");
        }
        if (!comment.getComment().equals(newCommentDto.getComment())) {
            comment.setComment(newCommentDto.getComment());
            comment.setChangedOn(LocalDateTime.now());
            comment = commentRepository.save(comment);
        }
        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found",
                    "The required object was not found.");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto get(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));

        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getList(Long eventId, List<Long> userIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        return CommentMapper.commentsToCommentsDto(commentRepository.search(eventId,
                Optional.ofNullable(userIds), Optional.ofNullable(rangeStart),
                Optional.ofNullable(rangeEnd), pageable));
    }

    @Override
    @Transactional
    public CommentDto patchByAdmin(NewCommentDto newCommentDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));

        if (!comment.getComment().equals(newCommentDto.getComment())) {
            comment.setComment(newCommentDto.getComment());
            comment.setChangedOn(LocalDateTime.now());
            comment = commentRepository.save(comment);
        }
        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));

        commentRepository.deleteById(commentId);
    }
}
