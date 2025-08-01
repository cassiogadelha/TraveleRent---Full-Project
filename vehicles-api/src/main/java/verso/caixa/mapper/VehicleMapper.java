package verso.caixa.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import verso.caixa.dto.AddAccessoryRequestDTO;
import verso.caixa.dto.CreateMaintenanceRequestDTO;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.dto.VehicleResponseDTO;
import verso.caixa.model.AccessoryModel;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi")
public interface VehicleMapper {
    @Mapping(target = "status", constant = "AVAILABLE")
    VehicleModel toEntity(CreateVehicleRequestDTO dto);

    AccessoryModel toEntity(AddAccessoryRequestDTO dto);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant())")
    MaintenanceModel toEntity(CreateMaintenanceRequestDTO dto);

    List<MaintenanceModel> toMaintenanceEntities(List<CreateMaintenanceRequestDTO> dtoList);
    Set<AccessoryModel> toAccessoryEntities(Set<AddAccessoryRequestDTO> dtoSet);

    @Mapping(target = "carTitle", expression = "java(vehicle.getModel() + \" \" + vehicle.getYear() + \" \" + vehicle.getEngine())")
    VehicleResponseDTO toResponseDTO(VehicleModel vehicle);

    List<VehicleResponseDTO> toResponseDTOList(List<VehicleModel> vehicles);

    @AfterMapping
    default void linkVehicle(@MappingTarget VehicleModel vehicle) {
        if (vehicle.getMaintenances() != null) {
            for (MaintenanceModel m : vehicle.getMaintenances()) {
                m.setVehicle(vehicle);
            }
        }
    }

}


