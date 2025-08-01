package verso.caixa.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.dto.UpdateVehicleStatusRequestDTO;
import verso.caixa.dto.VehicleResponseDTO;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.VehicleDeletionException;
import verso.caixa.exception.VehicleNotFoundException;
import verso.caixa.mapper.VehicleMapper;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;
import verso.caixa.repository.VehicleDAO;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Getter
@ApplicationScoped //cria somente uma instancia durante todo o ciclo de vida da aplicação
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleDAO vehicleDAO;

    public VehicleService(VehicleDAO vehicleDAO, VehicleMapper vehicleMapper) {
        this.vehicleDAO = vehicleDAO;
        this.vehicleMapper = vehicleMapper;

    }

    public Response createVehicle(CreateVehicleRequestDTO dto){
        try {
            VehicleModel vehicle = vehicleMapper.toEntity(dto);

            for (MaintenanceModel m : vehicle.getMaintenances()) {
                m.setVehicle(vehicle);
            }

            // Persistência em cascata
            vehicleDAO.persist(vehicle);

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


    public void deleteById(UUID vehicleId){
        VehicleModel vehicleToDelete = vehicleDAO.findById(vehicleId);

        if(vehicleToDelete == null)
            throw new VehicleDeletionException("Veículo não encontrado!", ErrorCode.VEHICLE_NOT_FOUND);

        if (vehicleToDelete.isRented())
            throw new VehicleDeletionException("Veículo não pode ser deletado pois está alugado!", ErrorCode.VEHICLE_RENTED_DELETE_DENIED);

        vehicleDAO.deleteById(vehicleId);
    }

    public Response getVehicleList(int page, int size) {
        PanacheQuery<VehicleModel> vehicles = vehicleDAO.findAll();
        vehicles.page(Page.of(page, size));

        if (vehicles.list().isEmpty()) {
            Map<String, String> response = Map.of("mensagem", "A lista de veículos está vazia."); //cria um map imutavel para ser convertido facilmente em Json
            return Response.ok(response).build();
        } else {
            return Response.ok(vehicleMapper.toResponseDTOList(vehicles.list())).build();
        }
    }

    public Response updateVehicle(UUID vehicleId, UpdateVehicleStatusRequestDTO dto) {
        VehicleModel vehicleModel = vehicleDAO.findById(vehicleId);

        if (vehicleModel == null) return Response.status(404).build();

        try {
            vehicleModel.setStatus(dto.newStatus());
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.noContent().build();
    }
}



