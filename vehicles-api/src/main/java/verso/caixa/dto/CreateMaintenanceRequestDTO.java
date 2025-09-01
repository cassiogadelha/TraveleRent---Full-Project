package verso.caixa.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CreateMaintenanceRequestDTO(
        UUID maintenanceId,

        @NotBlank
        String problemDescription,

        LocalDate createdAt
) {
}
