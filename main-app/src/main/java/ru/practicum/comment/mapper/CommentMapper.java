package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Comment newCommentDtoToComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(null, event, user, newCommentDto.getComment(), LocalDateTime.now(), null);
    }

    public static CommentDto commentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getEvent().getId(),
                UserMapper.userToUserShortDto(comment.getUser()),
                comment.getComment(), comment.getCreatedOn().format(dateTimeFormatter),
                comment.getChangedOn() != null ? comment.getChangedOn().format(dateTimeFormatter) : null);
    }

    public static List<CommentDto> commentsToCommentsDto(Iterable<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(commentToCommentDto(comment));
        }
        return commentDtos;
    }
}
