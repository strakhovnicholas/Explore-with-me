package ru.practicum.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "localDateTimeToString")
    EndpointHitDto toDto(EndpointHit hit);

    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "stringToLocalDateTime")
    EndpointHit toHit(EndpointHitDto hitDto);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime timestamp) {
        return timestamp != null ? timestamp.format(TIME_FORMAT) : null;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String timestamp) {
        return timestamp != null ? LocalDateTime.parse(timestamp, TIME_FORMAT) : null;
    }
}
