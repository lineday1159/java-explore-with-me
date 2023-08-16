package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.ConflictException;
import ru.practicum.error.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> get(Long userId) throws ParseException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));
        return RequestMapper.requestsToRequestsDto(requestRepository.findByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto save(Long userId, Long eventId) throws ParseException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=" + userId + " was not found", "The required object was not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=" + eventId + " was not found", "The required object was not found."));

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Incorrectly made request.", "Request must not be made by initiator");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Incorrectly made request.", "Event must be PUBLISHED");
        }
        if (event.getConfirmedRequests() + 1 > event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("Incorrectly made request.", "Participant Limit has been reached");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new ConflictException("Incorrectly made request.", "User has been already posted request");
        }
        RequestState requestState = !event.isRequestModeration() ||
                event.getParticipantLimit() == 0 ? RequestState.CONFIRMED : RequestState.PENDING;

        if (requestState.equals(RequestState.CONFIRMED)) {
            eventRepository.updateConfirmedRequestsById(eventId, 1);
        }

        ParticipationRequest participationRequest = requestRepository.save(new ParticipationRequest(null, new Date(),
                user, event, requestState));
        return RequestMapper.requestToRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) throws ParseException {
        ParticipationRequest participationRequest = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException(
                        "Request with id=" + requestId + " was not found", "The required object was not found."));
        if (participationRequest.getStatus().equals(RequestState.CONFIRMED)) {
            throw new ConflictException("Incorrectly made request.", "Request must be PENDING");
        }
        participationRequest.setStatus(RequestState.CANCELED);
        requestRepository.save(participationRequest);
        return RequestMapper.requestToRequestDto(participationRequest);
    }
}
