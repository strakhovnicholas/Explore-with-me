package ru.practicum.dto;

public record RequestParamDto(
        String start,
        String end,
        String[] uris,
        boolean unique
) {
}
