package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {
    private static final String API_PREFIX_HIT = "/hit";
    private static final String API_PREFIX_STATS = "/stats";
    private static final String API_PREFIX_STATS_COUNT = "/statsCount";

    public StatClient(@Value("${state-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getStats(Date start, Date end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get(API_PREFIX_STATS + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> getCount(String uri) {
        Map<String, Object> parameters = Map.of("eventUri", uri);
        return get(API_PREFIX_STATS_COUNT + "?eventUri={eventUri}", parameters);
    }

    public ResponseEntity<Object> postStats(StatDto statDto) {
        return post(API_PREFIX_HIT, statDto);
    }
}
