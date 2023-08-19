package ru.practicum.comment.dto;

import lombok.*;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDto {
    private Long id;
    private Long eventId;
    private UserShortDto user;
    private String comment;
    private String createdOn;
    private String changedOn;
}
