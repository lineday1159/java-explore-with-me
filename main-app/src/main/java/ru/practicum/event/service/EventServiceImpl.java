package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.error.BadRequestException;
import ru.practicum.error.ConflictException;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.UpdateRequestState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatClient statClient;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    @Override
    @Transactional
    public EventFullDto save(Long userId, NewEventDto newEventDto) throws ParseException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found",
                        "The required object was not found."));
        Date eventDate = dateFormatter.parse(newEventDto.getEventDate());
        Date currentDate = new Date();
        if (eventDate.toInstant().isBefore(currentDate.toInstant().plusSeconds(7200))) {
            throw new BadRequestException("Event date must be before current date + 2h",
                    "Some request parameters are not correct.");
        }
        Location location = locationRepository.save(EventMapper.locationDtoToLocation(newEventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.newEventDtoToEvent(user, newEventDto, location, category));
        return EventMapper.eventToEventFullDto(event, 0);
    }

    @Override
    @Transactional
    public EventFullDto patch(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) throws ParseException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        Category category = updateEventUserRequest.getCategory() == null ? null : categoryRepository.findById(updateEventUserRequest.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + updateEventUserRequest.getCategory() + " was not found",
                        "The required object was not found."));
        if (!event.getState().equals(EventState.CANCELED) && !event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Incorrectly made request.", "Event must not be published");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            Date eventDate = dateFormatter.parse(updateEventUserRequest.getEventDate());
            Date currentDate = new Date();
            if (eventDate.toInstant().isBefore(currentDate.toInstant().plusSeconds(7200))) {
                throw new BadRequestException("Event date must be before current date + 2h",
                        "Some request parameters are not correct.");
            }
        }
        Event newEvent = EventMapper.updateEventToEvent(event, updateEventUserRequest, category);

        Integer views = (Integer) statClient.getCount("/events/" + eventId).getBody();

        return EventMapper.eventToEventFullDto(eventRepository.save(newEvent), views);
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) throws ParseException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        Category category = updateEventAdminRequest.getCategory() == null ? null : categoryRepository.findById(updateEventAdminRequest.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + updateEventAdminRequest.getCategory() + " was not found",
                        "The required object was not found."));
        if (updateEventAdminRequest.getEventDate() != null) {
            Date eventDate = dateFormatter.parse(updateEventAdminRequest.getEventDate());
            Date currentDate = new Date();
            if (eventDate.toInstant().isBefore(currentDate.toInstant().plusSeconds(7200))) {
                throw new BadRequestException("Event date must be before current date + 2h",
                        "Some request parameters are not correct.");
            }
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Incorrectly made request.", "Event must not be published");
        }
        Event newEvent = EventMapper.updateEventAdminToEvent(event, updateEventAdminRequest, category);

        Integer views = (Integer) statClient.getCount("/events/" + eventId).getBody();

        return EventMapper.eventToEventFullDto(eventRepository.save(newEvent), views);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(Long userId, Long eventId) throws ParseException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found.");
        }
        Integer views = (Integer) statClient.getCount("/events/" + eventId).getBody();

        return EventMapper.eventToEventFullDto(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByPublic(Long eventId) throws ParseException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found.");
        }
        Integer views = (Integer) statClient.getCount("/events/" + eventId).getBody();
        return EventMapper.eventToEventFullDto(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getList(Long userId, int from, int size) throws ParseException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found."));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        return eventRepository.findByInitiatorId(userId, pageable)
                .stream()
                .map(event -> {
                    try {
                        Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                        return EventMapper.eventToEventShortDto(event, views);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getListByAdmin(List<Long> users, List<EventState> states, List<Long> categories, String rangeEnd, String rangeStart, int from, int size) throws ParseException {
        Optional<Date> startDate = rangeEnd != null ? Optional.ofNullable(dateFormatter.parse(rangeStart)) : Optional.empty();
        Optional<Date> endDate = rangeEnd != null ? Optional.ofNullable(dateFormatter.parse(rangeEnd)) : Optional.empty();
        log.info(startDate.toString() + "  " + endDate.toString());
        if (startDate.isPresent() && endDate.isPresent()) {
            if (startDate.get().after(endDate.get())) {
                throw new BadRequestException("Start date must be before end date",
                        "Some request parameters are not correct.");
            }
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        return eventRepository.searchByAdmin(Optional.ofNullable(users),
                Optional.ofNullable(states),
                Optional.ofNullable(categories),
                startDate,
                endDate,
                pageable).stream().map(
                event -> {
                    try {
                        Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                        return EventMapper.eventToEventFullDto(event, views);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getListByPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) throws ParseException {
        Optional<Date> startDate = rangeEnd != null ? Optional.ofNullable(dateFormatter.parse(rangeStart)) : Optional.ofNullable(new Date());
        Optional<Date> endDate = rangeEnd != null ? Optional.ofNullable(dateFormatter.parse(rangeEnd)) : Optional.empty();
        if (startDate.isPresent() && endDate.isPresent()) {
            if (startDate.get().after(endDate.get())) {
                throw new BadRequestException("Start date must be before end date",
                        "Some request parameters are not correct.");
            }
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        return eventRepository.searchByPublic(Optional.ofNullable(text),
                Optional.ofNullable(categories), Optional.ofNullable(paid),
                startDate, endDate, pageable).stream().map(
                event -> {
                    try {
                        Integer views = (Integer) statClient.getCount("/events/" + event.getId()).getBody();
                        return EventMapper.eventToEventShortDto(event, views);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) throws ParseException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", "The required object was not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found", "The required object was not found."));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Event with id=" + eventId + " was not found",
                    "The required object was not found.");
        }
        return RequestMapper.requestsToRequestsDto(requestRepository.findByEventId(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate) throws ParseException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=" + eventId + " was not found", "The required object was not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Event with id=" + eventId + " was not found",
                    "The required object was not found.");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        if (requestUpdate.getStatus().equals(UpdateRequestState.CONFIRMED)) {
            if (event.getParticipantLimit() < requestUpdate.getRequestIds().size() + event.getConfirmedRequests()) {
                throw new ConflictException("Incorrectly made request.", "Participant Limit has been reached");
            }
            requestRepository.updateRequestsStateByIds(requestUpdate.getRequestIds(), RequestState.CONFIRMED.name());
            eventRepository.updateConfirmedRequestsById(eventId, requestUpdate.getRequestIds().size());
            confirmedRequests = requestRepository.findAllByIdIn(requestUpdate.getRequestIds()).stream().map(request -> {
                try {
                    return RequestMapper.requestToRequestDto(request);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            rejectedRequests = requestRepository.findByEventIdAndStatus(eventId, RequestState.PENDING).stream()
                    .map(request -> {
                        request.setStatus(RequestState.CANCELED);
                        return request;
                    }).map(request -> {
                        try {
                            return RequestMapper.requestToRequestDto(request);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
            requestRepository.updateRequestsStateByEventAndState(eventId, RequestState.CANCELED.toString(),
                    RequestState.PENDING.toString());
        } else {
            if (!requestRepository.findAllByIdInAndStatus(requestUpdate.getRequestIds(), RequestState.CONFIRMED).isEmpty()) {
                throw new ConflictException("Incorrectly made request.", "Request must not be CONFIRMED");
            }
            requestRepository.updateRequestsStateByIds(requestUpdate.getRequestIds(), RequestState.REJECTED.name());
            rejectedRequests = requestRepository.findAllByIdIn(requestUpdate.getRequestIds()).stream().map(request -> {
                try {
                    return RequestMapper.requestToRequestDto(request);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
