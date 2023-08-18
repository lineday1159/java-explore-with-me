package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentMapper {

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    public static Comment newCommentDtoToComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(null, event, user, newCommentDto.getComment(), new Date());
    }

    public static CommentDto commentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getEvent().getId(),
                UserMapper.userToUserShortDto(comment.getUser()),
                comment.getComment(), dateFormatter.format(comment.getCreatedOn()));
    }

    public static List<CommentDto> commentsToCommentsDto(Iterable<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(commentToCommentDto(comment));
        }
        return commentDtos;
    }
}
