package verso.caixa.service;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.dto.UpdateVehicleStatusRequestDTO;
import verso.caixa.dto.VehicleResponseDTO;
import verso.caixa.enums.ErrorCode;
import verso.caixa.enums.VehicleStatusEnum;
import verso.caixa.exception.VehicleDeletionException;
import verso.caixa.exception.VehicleNotFoundException;
import verso.caixa.kafka.Producer;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.mapper.VehicleMapper;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;
import verso.caixa.repository.VehicleDAO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@ApplicationScoped //cria somente uma instancia durante todo o ciclo de vida da aplicação
public class VehicleService {

    @Inject
    @Channel("vehicle-maintenance")
    Emitter<UUID> emitter;

    public void publishVehicleMaintenance(UUID vehicleId) {
        emitter.send(vehicleId);
    }

    private final VehicleMapper vehicleMapper;
    private final VehicleDAO vehicleDAO;
    private final Producer producer;

    public VehicleService(VehicleDAO vehicleDAO, VehicleMapper vehicleMapper, Producer producer) {
        this.vehicleDAO = vehicleDAO;
        this.vehicleMapper = vehicleMapper;
        this.producer = producer;

    }

    public Response createVehicle(CreateVehicleRequestDTO dto){
        try {
            VehicleModel vehicle = vehicleMapper.toEntity(dto);

            for (MaintenanceModel m : vehicle.getMaintenances()) {
                m.setVehicle(vehicle);
            }

            // Persistência em cascata
            vehicleDAO.persist(vehicle);

            producer.publishVehicleCreation(new VehicleProducerDTO(
                    vehicle.vehicleId,
                    vehicle.getStatus().toString()
            ));

            URI location = URI.create("/api/v1/vehicles/" + vehicle.getVehicleId());
            VehicleResponseDTO responseDTO = vehicleMapper.toResponseDTO(vehicle);

            return Response.created(location)
                    .entity(responseDTO)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response findById(UUID vehicleId){
        Log.info("BOOKING AQUI");
        VehicleModel vehicle = vehicleDAO.findById(vehicleId);

        if (vehicle == null)
            throw new VehicleNotFoundException("Veículo não encontrado!", ErrorCode.VEHICLE_NOT_FOUND);

        VehicleResponseDTO responseDTO = vehicleMapper.toResponseDTO(vehicle);

        return Response.ok(responseDTO).build();
    }

    public VehicleModel getVehicleEntityById(UUID vehicleId) {
        VehicleModel vehicle = vehicleDAO.findById(vehicleId);
        if (vehicle == null)
            throw new VehicleNotFoundException("Veículo não encontrado!", ErrorCode.VEHICLE_NOT_FOUND);
        return vehicle;
    }

    @CacheInvalidateAll(cacheName = "all-vehicles")
    public void deleteById(UUID vehicleId){
        VehicleModel vehicleToDelete = vehicleDAO.findById(vehicleId);

        if(vehicleToDelete == null)
            throw new VehicleDeletionException("Veículo não encontrado!", ErrorCode.VEHICLE_NOT_FOUND);

        vehicleDAO.deleteById(vehicleId);
    }

    public List<VehicleResponseDTO> getVehicleListRaw(int page, int size) {
        PanacheQuery<VehicleModel> vehicles = vehicleDAO.findAll();
        vehicles.page(Page.of(page, size));
        return vehicleMapper.toResponseDTOList(vehicles.list());
    }

    public Response getVehicleList(int page, int size) {
        List<VehicleResponseDTO> vehicleListRaw = getVehicleListRaw(page, size);

        if (vehicleListRaw.isEmpty()) {
            Map<String, String> response = Map.of("mensagem", "A lista de veículos está vazia."); //cria um map imutavel para ser convertido facilmente em Json
            return Response.ok(response).build();
        } else {
            return Response.ok(vehicleListRaw).build();
        }
    }

    @CacheInvalidateAll(cacheName = "all-vehicles")
    public Response updateVehicle(UUID vehicleId, UpdateVehicleStatusRequestDTO dto) {
        VehicleModel vehicleModel = vehicleDAO.findById(vehicleId);

        if (vehicleModel == null) return Response.status(404).build();

        try {
            vehicleModel.setStatus(dto.newStatus());
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        if (dto.newStatus() == VehicleStatusEnum.UNDER_MAINTENANCE)
            publishVehicleMaintenance(vehicleId);

        return Response.noContent().build();
    }

    @Transactional
    public Response createVehicleList(@Valid List<@Valid CreateVehicleRequestDTO> dtoList) {

        producer.sendVehicleCreationList(dtoList);
        return Response.accepted().entity("Veículos enviados para processamento").build();
    }

    @Transactional
    public void createVehicleFromKafka(CreateVehicleRequestDTO dto) {
        VehicleModel vehicle = vehicleMapper.toEntity(dto);

        for (MaintenanceModel m : vehicle.getMaintenances()) {
            m.setVehicle(vehicle);
        }

        vehicleDAO.persist(vehicle);

        producer.publishVehicleCreation(new VehicleProducerDTO(
                vehicle.getVehicleId(),
                vehicle.getStatus().toString()
        ));
    }
}



