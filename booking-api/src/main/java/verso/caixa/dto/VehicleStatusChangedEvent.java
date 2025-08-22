package verso.caixa.dto;

import java.time.Instant;
import java.util.UUID;

public record VehicleStatusChangedEvent(
        UUID vehicleId,
        String newStatus,
        Instant activatedAt
) {
}
