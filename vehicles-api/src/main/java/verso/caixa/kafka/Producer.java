package verso.caixa.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import verso.caixa.dto.CreateVehicleRequestDTO;

import java.util.List;

public class Producer {

    @Inject
    @Channel("vehicle-created")
    Emitter<VehicleProducerDTO> emitter;

    @Inject
    @Channel("vehicle-creation-list-out")
    Emitter<String> vehicleListEmitter;

    private final ObjectMapper mapper;

    public Producer(
            ObjectMapper mapper
            ) {
        this.mapper = mapper;
    }

    public void publishVehicleCreation(VehicleProducerDTO dto){
        emitter.send(dto);
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
