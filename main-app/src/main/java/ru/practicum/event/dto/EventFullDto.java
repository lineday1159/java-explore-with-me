package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private String eventDate;
    private String publishedOn;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private int confirmedRequests;
    private String createdOn;
    private UserShortDto initiator;
    private EventState state;
    private long views;
}
