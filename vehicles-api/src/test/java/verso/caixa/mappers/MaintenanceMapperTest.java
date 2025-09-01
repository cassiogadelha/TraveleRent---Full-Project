package verso.caixa.mappers;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import verso.caixa.dto.CreateMaintenanceRequestDTO;
import verso.caixa.dto.MaintenanceResponseDTO;
import verso.caixa.dto.VehicleInfoDTO;
import verso.caixa.enums.VehicleStatusEnum;
import verso.caixa.mapper.MaintenanceMapper;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class MaintenanceMapperTest {
    @Inject
    MaintenanceMapper mapper;

    @Test
    void shouldMapCreateMaintenanceRequestDTOToEntity() {
        CreateMaintenanceRequestDTO dto = new CreateMaintenanceRequestDTO(
                UUID.randomUUID(),
                "Troca de óleo",
                LocalDate.now()
        );

        MaintenanceModel entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Troca de óleo", entity.getProblemDescription());
        assertEquals(dto.createdAt(), entity.getCreatedAt());
    }

    @Test
    void shouldMapMaintenanceModelToResponseDTO() {
        VehicleModel vehicle = new VehicleModel();
        vehicle.setModel("Gol");
        vehicle.setBrand("Volkswagen");
        vehicle.setYear(2018);
        vehicle.setEngine("1.6");

        MaintenanceModel maintenance = new MaintenanceModel();
        maintenance.setMaintenanceId(UUID.randomUUID());
        maintenance.setProblemDescription("Alinhamento");
        maintenance.setCreatedAt(LocalDate.now());
        maintenance.setVehicleModel(vehicle);

        MaintenanceResponseDTO response = mapper.toResponse(maintenance);

        assertNotNull(response);
        assertEquals(maintenance.maintenanceId, response.maintenanceId());
        assertEquals("Alinhamento", response.problemDescription());
        assertEquals(maintenance.getCreatedAt(), response.createdAt());

        VehicleInfoDTO vehicleInfo = response.vehicleInfo();
        assertNotNull(vehicleInfo);
        assertEquals("Gol", vehicleInfo.model());
        assertEquals("Volkswagen", vehicleInfo.brand());
        assertEquals(2018, vehicleInfo.year());
        assertEquals("1.6", vehicleInfo.engine());
        assertEquals(VehicleStatusEnum.AVAILABLE, vehicleInfo.status());
    }
}
