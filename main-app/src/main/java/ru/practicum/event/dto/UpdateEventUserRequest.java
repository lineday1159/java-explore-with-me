package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.StateAction;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    private StateAction stateAction;
    private Integer participantLimit;
}
