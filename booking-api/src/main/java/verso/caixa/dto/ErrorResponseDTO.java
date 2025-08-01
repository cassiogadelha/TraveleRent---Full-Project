package verso.caixa.dto;

import java.time.Instant;

public record ErrorResponseDTO(
        String title,
        String details,
        int statusCode,
        String path,
        Instant timestamp,
        String errorCode
) {
}
