package verso.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Getter;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.VehicleStatusDAO;

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
}
