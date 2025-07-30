package verso.caixa.dto;

import java.time.Instant;
import java.util.UUID;

public record MaintenanceResponseDTO(
        UUID maintenanceId,
        String problemDescription,
        Instant createdAt,
        VehicleInfoDTO vehicleInfo
) {
}
