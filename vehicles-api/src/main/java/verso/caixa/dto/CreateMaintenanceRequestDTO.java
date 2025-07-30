package verso.caixa.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record CreateMaintenanceRequestDTO(
        UUID maintenanceId,

        @NotBlank
        String problemDescription,

        Instant createdAt
) {
}
