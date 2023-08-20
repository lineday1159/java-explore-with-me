package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto requestToRequestDto(ParticipationRequest request) throws ParseException {
        return new ParticipationRequestDto(request.getId(), request.getCreatedOn().format(dateTimeFormatter),
                request.getRequester().getId(), request.getEvent().getId(), request.getStatus());
    }

    public static List<ParticipationRequestDto> requestsToRequestsDto(Iterable<ParticipationRequest> requests) throws ParseException {
        List<ParticipationRequestDto> requestDtos = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            requestDtos.add(requestToRequestDto(request));
        }
        return requestDtos;
    }
}
