package verso.caixa.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import verso.caixa.dto.CreateVehicleRequestDTO;

import java.util.List;
import java.util.UUID;

public class Producer {

    @Inject
    @Channel("vehicle-created")
    Emitter<VehicleProducerDTO> vehicleCreatedEmitter;

    @Inject
    @Channel("vehicle-creation-list-out")
    Emitter<String> vehicleListEmitter;

    @Inject
    @Channel("vehicle-status-changed")
    Emitter<VehicleProducerDTO> emitterVehicleStatusChanged;


    private final ObjectMapper mapper;

    public Producer(
            ObjectMapper mapper
            ) {
        this.mapper = mapper;
    }

    public void publishVehicleStatusChanged(VehicleProducerDTO dto){
        emitterVehicleStatusChanged.send(dto);
    }

    public void publishVehicleCreation(VehicleProducerDTO dto){
        vehicleCreatedEmitter.send(dto);
    }

    public void sendVehicleCreationList(List<CreateVehicleRequestDTO> dtoList) {
        try {
            String json = mapper.writeValueAsString(dtoList);
            vehicleListEmitter.send(json);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar DTO", e);
        }
    }

}
