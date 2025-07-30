package verso.caixa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import verso.caixa.dto.MaintenanceResponseDTO;
import verso.caixa.dto.VehicleInfoDTO;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;

@Mapper(componentModel = "cdi")
public interface MaintenanceMapper {
    @Mapping(target = "vehicleInfo", expression = "java(toVehicleInfo(model.getVehicleModel()))")
    MaintenanceResponseDTO toResponse(MaintenanceModel model);

    default VehicleInfoDTO toVehicleInfo(VehicleModel vehicle) {
        return new VehicleInfoDTO(
                vehicle.getVehicleId(),
                vehicle.getModel(),
                vehicle.getBrand(),
                vehicle.getYear(),
                vehicle.getEngine(),
                vehicle.getStatus(),
                vehicle.getCarTitle()
        );
    }

}

