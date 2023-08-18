package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.error.ConflictException;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Date;
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

        ParticipationRequest request = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (request == null) {
            throw new ConflictException(
                    "User with id=" + userId + " must be a participant", "The comment can not be published.");
        }
        if (!request.getStatus().equals(RequestState.CONFIRMED)) {
            throw new ConflictException(
                    "User with id=" + userId + " must be a participant", "The comment can not be published.");
        }

        Comment comment = commentRepository.save(CommentMapper.newCommentDtoToComment(newCommentDto, user, event));
        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(UpdateCommentDto updateCommentDto, Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found",
                    "The required object was not found.");
        }
        if (!comment.getComment().equals(updateCommentDto.getComment())) {
            comment.setComment(updateCommentDto.getComment());
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
    public List<CommentDto> getList(Long eventId, List<Long> userIds, Date rangeStart, Date rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        return CommentMapper.commentsToCommentsDto(commentRepository.search(eventId,
                Optional.ofNullable(userIds), Optional.ofNullable(rangeStart),
                Optional.ofNullable(rangeEnd), pageable));
    }
}
