package verso.caixa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import verso.caixa.enums.VehicleStatusEnum;

import java.util.List;

public record CreateVehicleRequestDTO(
    @NotBlank(message = "model não pode ser vazio")
    String model,

    VehicleStatusEnum status,

    @NotNull(message = "year não pode ser nulo")
    @Min(value = 1980, message = "year deve ser no mínimo 1980")
    Integer year,

    @NotBlank(message = "engine não pode ser vazio")
    String engine,

    @NotBlank(message = "brand não pode ser vazio")
    String brand,

    List<AddAccessoryRequestDTO> accessories,
    List<CreateMaintenanceRequestDTO> maintenances
) {
}
