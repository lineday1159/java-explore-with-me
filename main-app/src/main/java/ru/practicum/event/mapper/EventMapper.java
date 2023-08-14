package ru.practicum.event.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventMapper {
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    public static LocationDto locationToLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    public static Location locationDtoToLocation(LocationDto locationDto) {
        return new Location(null, locationDto.getLat(), locationDto.getLon());
    }

    public static Event newEventDtoToEvent(User user, NewEventDto newEventDto, Location location, Category category) throws ParseException {
        return new Event(null, newEventDto.getTitle(),
                newEventDto.getAnnotation(), category, newEventDto.getDescription(),
                dateFormatter.parse(newEventDto.getEventDate()), new Date(), null,
                location, user, newEventDto.getPaid() != null ? newEventDto.getPaid() : false,
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true,
                EventState.PENDING, 0, 0);
    }

    public static EventFullDto eventToEventFullDto(Event event) throws ParseException {
        CategoryDto categoryDto = CategoryMapper.categoryToCategoryDto(event.getCategory());
        UserShortDto userShortDto = UserMapper.userToUserShortDto(event.getInitiator());
        LocationDto locationDto = locationToLocationDto(event.getLocation());
        return new EventFullDto(event.getId(), event.getTitle(), event.getAnnotation(),
                categoryDto, event.getDescription(), dateFormatter.format(event.getEventDate()),
                event.getPublishedOn() != null ? dateFormatter.format(event.getPublishedOn()) : null,
                locationDto, event.isPaid(), event.getParticipantLimit(), event.isRequestModeration(),
                event.getConfirmedRequests(), dateFormatter.format(event.getCreatedOn()), userShortDto,
                event.getState(), event.getViews());
    }

    public static EventShortDto eventToEventShortDto(Event event) throws ParseException {
        CategoryDto categoryDto = CategoryMapper.categoryToCategoryDto(event.getCategory());
        UserShortDto userShortDto = UserMapper.userToUserShortDto(event.getInitiator());
        return new EventShortDto(event.getId(), event.getTitle(), event.getAnnotation(),
                categoryDto, dateFormatter.format(event.getEventDate()), event.isPaid(),
                event.getConfirmedRequests(), userShortDto, event.getViews());
    }

    public static Event updateEventToEvent(Event oldEvent, UpdateEventUserRequest updateEvent, Category category) throws ParseException {
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            oldEvent.setCategory(category);
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getEventDate() != null) {
            oldEvent.setEventDate(dateFormatter.parse(updateEvent.getEventDate()));
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                oldEvent.setState(EventState.CANCELED);
            }
            if (updateEvent.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                oldEvent.setState(EventState.PENDING);
            }
        }
        return oldEvent;
    }

    public static Event updateEventAdminToEvent(Event oldEvent, UpdateEventAdminRequest updateEvent, Category category) throws ParseException {
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            oldEvent.setCategory(category);
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getEventDate() != null) {
            oldEvent.setEventDate(dateFormatter.parse(updateEvent.getEventDate()));
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.REJECT_EVENT)) {
                oldEvent.setState(EventState.CANCELED);
            }
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                oldEvent.setState(EventState.PUBLISHED);
                oldEvent.setPublishedOn(new Date());
            }
        }
        return oldEvent;
    }
}
