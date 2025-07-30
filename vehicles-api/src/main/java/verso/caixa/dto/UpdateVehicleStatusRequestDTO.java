package verso.caixa.dto;

import verso.caixa.enums.VehicleStatusEnum;

public record UpdateVehicleStatusRequestDTO(
    VehicleStatusEnum newStatus
) {
}
