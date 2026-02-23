package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsJpaRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(
                h.app, 
                h.uri, 
                COUNT(h.ip)
            ) 
            FROM EndpointHit h 
            WHERE h.timestamp BETWEEN :start AND :end 
            GROUP BY h.uri, h.app 
            ORDER BY COUNT(h.id) DESC
            """)
    List<ViewStatsDto> getStatsNotUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(
                h.app, 
                h.uri, 
                COUNT(DISTINCT h.ip)
            ) 
            FROM EndpointHit h 
            WHERE h.timestamp BETWEEN :start AND :end 
            GROUP BY h.uri, h.app 
            ORDER BY COUNT(h.id) DESC
            """)
    List<ViewStatsDto> getStatsUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(
                h.app, 
                h.uri, 
                COUNT(h.ip)
            ) 
            FROM EndpointHit h 
            WHERE h.uri IN :uris 
                AND h.timestamp BETWEEN :start AND :end 
            GROUP BY h.uri, h.app 
            ORDER BY COUNT(h.id) DESC
            """)
    List<ViewStatsDto> getStatsNotUniqueWithUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(
                h.app, 
                h.uri, 
                COUNT(DISTINCT h.ip)
            ) 
            FROM EndpointHit h 
            WHERE h.uri IN :uris 
                AND h.timestamp BETWEEN :start AND :end 
            GROUP BY h.uri, h.app 
            ORDER BY COUNT(h.id) DESC
            """)
    List<ViewStatsDto> getStatsUniqueWithUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}