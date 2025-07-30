package verso.caixa.dto;

import verso.caixa.enums.VehicleStatusEnum;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record VehicleResponseDTO(
        UUID vehicleId,
        String model,
        VehicleStatusEnum status,
        int year,
        String engine,
        String brand,
        String carTitle,
        Set<AccessoryResponseDTO> accessories,
        List<MaintenanceResponseDTO> maintenances
) {
}
