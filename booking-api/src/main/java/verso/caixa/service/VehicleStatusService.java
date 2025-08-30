package verso.caixa.service;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.jetbrains.annotations.Debug;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.model.BookingModel;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.VehicleStatusDAO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Getter
public class VehicleStatusService {

    private final VehicleStatusDAO vehicleStatusDAO;

    public VehicleStatusService(VehicleStatusDAO vehicleStatusDAO) {
        this.vehicleStatusDAO = vehicleStatusDAO;
    }

    @Transactional
    public void addVehicle(VehicleProducerDTO dto) {
        VehicleStatus vehicleStatus = new VehicleStatus(dto.vehicleId(), dto.vehicleStatus());

        vehicleStatusDAO.persist(vehicleStatus);
    }

    public Response getVehicleStatus(int page, int size) {
        List<VehicleStatus> vehicleStatusList;

        vehicleStatusList = vehicleStatusDAO.findAll()
                .page(Page.of(page, size))
                .list();

        if (vehicleStatusList.isEmpty()) {
            Map<String, String> response = Map.of("mensagem", "A lista de VehiclesStatus está vazia.");
            return Response.ok(response).build();
        }

        return Response.ok(vehicleStatusList).build();
    }

    @Transactional
    public void changeVehicleStatus(VehicleProducerDTO dto) {

        VehicleStatus vehicleStatus = vehicleStatusDAO.findById(dto.vehicleId());
        Log.info("VEHICLE TO CHANGE: " + dto.vehicleId());

        if (vehicleStatus != null) {
            vehicleStatus.setStatus(dto.vehicleStatus());
        }
    }
}
