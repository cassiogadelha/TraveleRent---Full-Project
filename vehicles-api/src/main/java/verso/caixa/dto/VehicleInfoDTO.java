package verso.caixa.dto;

import verso.caixa.enums.VehicleStatusEnum;

import java.util.UUID;

public record VehicleInfoDTO(
        UUID vehicleId,
        String model,
        String brand,
        int year,
        String engine,
        VehicleStatusEnum status,
        String carTitle
) {
}
