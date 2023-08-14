package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.StateAction;

import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class UpdateEventAdminRequest {
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
