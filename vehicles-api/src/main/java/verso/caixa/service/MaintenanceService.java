package verso.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateMaintenanceRequestDTO;
import verso.caixa.dto.MaintenanceResponseDTO;
import verso.caixa.enums.VehicleStatusEnum;
import verso.caixa.kafka.Producer;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.mapper.MaintenanceMapper;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;
import verso.caixa.repository.MaintenanceDAO;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MaintenanceService {

    private final MaintenanceMapper maintenanceMapper;
    private final MaintenanceDAO maintenanceDAO;
    private final VehicleService vehicleService;
    private final Producer producer;

    public MaintenanceService(MaintenanceMapper maintenanceMapper, MaintenanceDAO maintenanceDAO, VehicleService vehicleService, Producer producer){
        this.maintenanceMapper = maintenanceMapper;
        this.maintenanceDAO = maintenanceDAO;
        this.vehicleService = vehicleService;
        this.producer = producer;
    }

    public Response addMaintenance(UUID vehicleId, CreateMaintenanceRequestDTO dto) {
        VehicleModel vehicle = vehicleService.getVehicleEntityById(vehicleId);

        MaintenanceModel maintenance = maintenanceMapper.toEntity(dto);

        vehicle.moveForMaintenance(maintenance);

        maintenanceDAO.persist(maintenance);

        producer.publishVehicleStatusChanged(new VehicleProducerDTO(
                vehicleId,
                VehicleStatusEnum.UNDER_MAINTENANCE.toString()
        ));

        return Response.created(URI.create("/api/v1/vehicles/%s/maintenances/%s".formatted(
                vehicleId, maintenance.getMaintenanceId()
        ))).build();
    }

    public Response findById(UUID vehicleId, UUID maintenanceId) {

        MaintenanceModel maintenance = MaintenanceModel.findByVehicleAndMaintenanceId(vehicleId, maintenanceId);

        if (maintenance == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        MaintenanceResponseDTO response = maintenanceMapper.toResponse(maintenance);

        return Response.ok(response).build();
    }
}
