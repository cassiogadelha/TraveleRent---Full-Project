package verso.caixa.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.service.VehicleService;

import java.util.List;

@ApplicationScoped
public class Consumer {

    private final ObjectMapper mapper;

    VehicleService vehicleService;

    public Consumer(
            VehicleService vehicleService,
            ObjectMapper objectMapper
    ){
        this.vehicleService = vehicleService;
        this.mapper = objectMapper;
    }

    @Incoming("vehicle-creation-list-in")
    @Transactional
    public void consumeVehicle(String json) {

        try {
            List<CreateVehicleRequestDTO> list = mapper.readValue(json, new TypeReference<>() {});

            Log.info("JSON recebido: " + json);
            Log.info("Quantidade de veículos: " + list.size());

            for (CreateVehicleRequestDTO vehicleDTO : list) {
                try {
                    vehicleService.createVehicleFromKafka(vehicleDTO);
                    Log.info("Veículo criado: " + vehicleDTO.model());
                } catch (Exception ex) {
                    Log.error("Erro ao criar veículo: " + vehicleDTO.model(), ex);
                }
            }

        } catch (Exception e) {
            Log.error(e);
        }
    }
}
