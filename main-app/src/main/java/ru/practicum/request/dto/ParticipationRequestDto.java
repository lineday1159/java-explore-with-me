package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.request.model.RequestState;

@Setter
@ToString
@Getter
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private String created;
    private Long requester;
    private Long event;
    private RequestState status;
}
