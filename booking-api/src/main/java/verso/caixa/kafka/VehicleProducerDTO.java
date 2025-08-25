package verso.caixa.kafka;

import java.util.UUID;

public record VehicleProducerDTO(
        UUID vehicleId,
        String vehicleStatus
) {

}
