package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.RequestParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<String> postHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Statistic service: сохранен запрос для эндпоинта {}", hitDto.uri());
        statsService.saveHit(hitDto);
        return new ResponseEntity<>("Информация сохранена", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam(name = "start") String start,
                                       @RequestParam(name = "end") String end,
                                       @RequestParam(name = "uris", required = false) String[] uris,
                                       @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("Statistic service: запрошена статистика для эндпоинтов {}", uris);
        RequestParamDto requestDto = new RequestParamDto(start, end, uris, unique);
        return statsService.getStats(requestDto);
    }

    @GetMapping("/hits")
    public List<EndpointHitDto> getAllHits() {
        log.info("Statistic service: запрошена вся статистика");
        return statsService.getAllHits();
    }

}
