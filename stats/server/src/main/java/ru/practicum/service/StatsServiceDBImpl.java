package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitMapper;
import ru.practicum.dto.RequestParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.repository.StatsJpaRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatsServiceDBImpl implements StatsService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsJpaRepository statsJpaRepository;
    private final EndpointHitMapper endpointHitMapper;

    public void saveHit(EndpointHitDto hitDto) {
        statsJpaRepository.save(endpointHitMapper.toHit(hitDto));
    }

    public List<EndpointHitDto> getAllHits() {
        return statsJpaRepository.findAll().stream()
                .map(endpointHitMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ViewStatsDto> getStats(RequestParamDto requestParamDto) {
        String startDecoded = URLDecoder.decode(requestParamDto.start(), StandardCharsets.UTF_8);
        String endDecoded = URLDecoder.decode(requestParamDto.end(), StandardCharsets.UTF_8);

        LocalDateTime start = LocalDateTime.parse(startDecoded, TIME_FORMAT);
        LocalDateTime end = LocalDateTime.parse(endDecoded, TIME_FORMAT);

        String[] uris = requestParamDto.uris();
        if (requestParamDto.unique()) {
            if (uris == null) {
                return statsJpaRepository.getStatsUnique(start, end);
            } else {
                return statsJpaRepository.getStatsUniqueWithUris(start, end, List.of(uris));
            }
        } else {
            if (uris == null) {
                return statsJpaRepository.getStatsNotUnique(start, end);
            } else {
                return statsJpaRepository.getStatsNotUniqueWithUris(start, end, List.of(uris));
            }
        }
    }
}
