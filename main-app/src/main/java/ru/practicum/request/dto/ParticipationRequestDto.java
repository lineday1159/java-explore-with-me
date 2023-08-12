package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.request.model.RequestState;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private String created;
    private Long requester;
    private Long event;
    private RequestState status;
}
