package com.example.reservation;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message, //для пользоват
        String detailedMessage, //errorMessage, для логов
        LocalDateTime errorTime
) {
}
