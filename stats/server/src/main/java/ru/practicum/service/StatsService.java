package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.RequestParamDto;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHitDto hitDto);

    List<EndpointHitDto> getAllHits();

    List<ViewStatsDto> getStats(RequestParamDto requestParamDto);
}