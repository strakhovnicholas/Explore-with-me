package ru.practicum;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatsClient {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SERVER_URL = "http://ewm-stat-server:9090";
    private static final RestTemplate rest;

    static {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(SERVER_URL))
                .build();
    }

    private StatsClient() {
    }

    public static List<ViewStatsDto> getStats(LocalDateTime startTime, LocalDateTime endTime,
                                               @Nullable String[] uris, @Nullable Boolean unique) {
        String startEncoded = encodeDateTime(startTime);
        String endEncoded = encodeDateTime(endTime);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", startEncoded)
                .queryParam("end", endEncoded);

        if (uris != null) {
            uriBuilder.queryParam("uris", (Object[]) uris);
        }
        if (unique != null) {
            uriBuilder.queryParam("unique", unique);
        }

        return makeAndSendGetStatsRequest(uriBuilder.build().toString(), null);
    }

    public static ResponseEntity<String> postHit(EndpointHitDto hit) {
        return makeAndSendPostHitRequest("/hit", hit);
    }

    public static Map<Integer, Long> getMapIdViews(Collection<Integer> eventsId) {
        if (eventsId == null || eventsId.isEmpty()) {
            return Collections.emptyMap();
        }

        String[] uriArray = eventsId.stream()
                .map(id -> "/events/" + id)
                .toArray(String[]::new);

        List<ViewStatsDto> endpointStatsList = getStats(
                LocalDateTime.of(1970, 1, 1, 0, 0),
                LocalDateTime.now(),
                uriArray,
                true
        );

        if (endpointStatsList == null || endpointStatsList.isEmpty()) {
            return eventsId.stream()
                    .collect(Collectors.toMap(id -> id, id -> 0L));
        }

        return endpointStatsList.stream()
                .collect(Collectors.toMap(
                        StatsClient::extractIdFromUri,
                        ViewStatsDto::hits,
                        (v1, v2) -> v1
                ));
    }

    private static String encodeDateTime(LocalDateTime dateTime) {
        return URLEncoder.encode(dateTime.format(TIME_FORMAT), StandardCharsets.UTF_8);
    }

    private static Integer extractIdFromUri(ViewStatsDto stats) {
        String[] splitUri = stats.uri().split("/");
        return Integer.valueOf(splitUri[splitUri.length - 1]);
    }

    private static List<ViewStatsDto> makeAndSendGetStatsRequest(String path, @Nullable Map<String, Object> parameters) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders());

        try {
            ResponseEntity<List<ViewStatsDto>> response;
            if (parameters != null) {
                response = rest.exchange(
                        path,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<List<ViewStatsDto>>() {},
                        parameters
                );
            } else {
                response = rest.exchange(
                        path,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<List<ViewStatsDto>>() {}
                );
            }
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    private static ResponseEntity<String> makeAndSendPostHitRequest(String path, EndpointHitDto hit) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, defaultHeaders());

        try {
            return rest.exchange(path, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private static HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}