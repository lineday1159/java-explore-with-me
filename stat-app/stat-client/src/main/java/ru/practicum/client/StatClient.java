package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class StatClient extends BaseClient {
    private static final String API_PREFIX_HIT = "/hit";
    private static final String API_PREFIX_STATS = "/stats";
    private static final String API_PREFIX_STATS_COUNT = "/statsCount";
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    public StatClient(@Value("${state-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getStats(Date start, Date end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", dateFormatter.format(start),
                "end", dateFormatter.format(end),
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<Object> responseEntity = get(API_PREFIX_STATS + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        return responseEntity;
    }

    public ResponseEntity<Object> postStats(StatDto statDto) {
        return post(API_PREFIX_HIT, statDto);
    }
}
