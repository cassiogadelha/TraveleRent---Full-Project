package verso.caixa.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.AddAccessoryRequestDTO;
import verso.caixa.model.AccessoryModel;
import verso.caixa.model.VehicleModel;

import java.util.UUID;

@ApplicationScoped
public class AccessoryService {

    private final VehicleService vehicleService;

    public AccessoryService(VehicleService vehicleService){
        this.vehicleService = vehicleService;
    }

    public Response addAccessory(UUID id, AddAccessoryRequestDTO dto) {

        VehicleModel vehicle = vehicleService.getVehicleEntityById(id);

        AccessoryModel accessory = new AccessoryModel(dto.name());

        accessory.persist();

        vehicle.addAccessory(accessory);

        Log.info(vehicle.getAccessories());

        return Response.noContent().build();

    }
}
