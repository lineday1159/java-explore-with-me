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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventMapper {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocationDto locationToLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    public static Location locationDtoToLocation(LocationDto locationDto) {
        return new Location(null, locationDto.getLat(), locationDto.getLon());
    }

    public static Event newEventDtoToEvent(User user, NewEventDto newEventDto, Location location, Category category) {
        return new Event(null, newEventDto.getTitle(),
                newEventDto.getAnnotation(), category, newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter), LocalDateTime.now(), null,
                location, user, newEventDto.getPaid() != null ? newEventDto.getPaid() : false,
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true,
                EventState.PENDING, 0);
    }

    public static EventFullDto eventToEventFullDto(Event event, Long views) {
        CategoryDto categoryDto = CategoryMapper.categoryToCategoryDto(event.getCategory());
        UserShortDto userShortDto = UserMapper.userToUserShortDto(event.getInitiator());
        LocationDto locationDto = locationToLocationDto(event.getLocation());
        return new EventFullDto(event.getId(), event.getTitle(), event.getAnnotation(),
                categoryDto, event.getDescription(), event.getEventDate().format(dateTimeFormatter),
                event.getPublishedOn() != null ? event.getPublishedOn().format(dateTimeFormatter) : null,
                locationDto, event.isPaid(), event.getParticipantLimit(), event.isRequestModeration(),
                event.getConfirmedRequests(), event.getCreatedOn().format(dateTimeFormatter), userShortDto,
                event.getState(), views);
    }

    public static List<EventFullDto> eventsToEventsFullDto(Iterable<Event> events, HashMap<Long, Long> viewsMap) {
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(eventToEventFullDto(event,
                    viewsMap.get(event.getId()) == null ? 0 : viewsMap.get(event.getId())));
        }
        return eventFullDtoList;
    }


    public static EventShortDto eventToEventShortDto(Event event, Long views) {
        CategoryDto categoryDto = CategoryMapper.categoryToCategoryDto(event.getCategory());
        UserShortDto userShortDto = UserMapper.userToUserShortDto(event.getInitiator());
        return new EventShortDto(event.getId(), event.getTitle(), event.getAnnotation(),
                categoryDto, event.getEventDate().format(dateTimeFormatter), event.isPaid(),
                event.getConfirmedRequests(), userShortDto, views);
    }

    public static List<EventShortDto> eventsToEventsShortDto(Iterable<Event> events, HashMap<Long, Long> viewsMap) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : events) {
            eventShortDtoList.add(eventToEventShortDto(event,
                    viewsMap.get(event.getId()) == null ? 0 : viewsMap.get(event.getId())));
        }
        return eventShortDtoList;
    }

    public static Event updateEventToEvent(Event oldEvent, UpdateEventUserRequest updateEvent, Category category) {
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
            oldEvent.setEventDate(LocalDateTime.parse(updateEvent.getEventDate(), dateTimeFormatter));
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

    public static Event updateEventAdminToEvent(Event oldEvent, UpdateEventAdminRequest updateEvent, Category category) {
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
            oldEvent.setEventDate(LocalDateTime.parse(updateEvent.getEventDate(), dateTimeFormatter));
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.REJECT_EVENT)) {
                oldEvent.setState(EventState.CANCELED);
            }
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                oldEvent.setState(EventState.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());
            }
        }
        return oldEvent;
    }
}
