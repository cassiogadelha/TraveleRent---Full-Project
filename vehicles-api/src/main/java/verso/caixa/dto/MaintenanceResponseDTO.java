package verso.caixa.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceResponseDTO(
        UUID maintenanceId,
        String problemDescription,
        LocalDate createdAt,
        VehicleInfoDTO vehicleInfo
) {
}
