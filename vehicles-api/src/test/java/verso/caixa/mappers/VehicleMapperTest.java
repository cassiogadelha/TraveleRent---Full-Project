package verso.caixa.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import verso.caixa.dto.AddAccessoryRequestDTO;
import verso.caixa.dto.CreateMaintenanceRequestDTO;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.dto.VehicleResponseDTO;
import verso.caixa.enums.VehicleStatusEnum;
import verso.caixa.mapper.VehicleMapper;
import verso.caixa.model.AccessoryModel;
import verso.caixa.model.MaintenanceModel;
import verso.caixa.model.VehicleModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private VehicleMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(VehicleMapper.class);
    }

    @Test
    void shouldMapCreateVehicleRequestDTOToVehicleModel() {
        CreateVehicleRequestDTO dto = new CreateVehicleRequestDTO(
                "Uno",
                VehicleStatusEnum.AVAILABLE,
                2020,
                "1.0",
                "Fiat",
                null,
                null);

        VehicleModel model = mapper.toEntity(dto);

        assertNotNull(model);
        assertEquals(VehicleStatusEnum.AVAILABLE, model.getStatus());
        assertEquals("Fiat", model.getBrand());
        assertEquals("Uno", model.getModel());
        assertEquals("1.0", model.getEngine());
        assertEquals(2020, model.getYear());
    }

    @Test
    void shouldMapAddAccessoryRequestDTOToAccessoryModel() {
        AddAccessoryRequestDTO dto = new AddAccessoryRequestDTO("Airbag");

        AccessoryModel model = mapper.toEntity(dto);

        assertNotNull(model);
        assertEquals("Airbag", model.getName());
    }

    @Test
    void shouldMapCreateMaintenanceRequestDTOToMaintenanceModel() {
        CreateMaintenanceRequestDTO dto = new CreateMaintenanceRequestDTO(UUID.randomUUID(), "Troca de óleo", LocalDate.now());

        MaintenanceModel model = mapper.toEntity(dto);

        assertNotNull(model);
        assertEquals("Troca de óleo", model.getProblemDescription());
        assertEquals(dto.createdAt(), model.getCreatedAt());
        assertNotNull(model.getCreatedAt());
    }

    @Test
    void shouldMapListOfMaintenanceDTOsToEntities() {
        List<CreateMaintenanceRequestDTO> dtos = List.of(
                new CreateMaintenanceRequestDTO(UUID.randomUUID(), "Revisão", LocalDate.now())
        );

        List<MaintenanceModel> models = mapper.toMaintenanceEntities(dtos);

        assertEquals(1, models.size());
        assertEquals("Revisão", models.getFirst().getProblemDescription());
    }

    @Test
    void shouldMapSetOfAccessoryDTOsToEntities() {
        Set<AddAccessoryRequestDTO> dtos = Set.of(new AddAccessoryRequestDTO("GPS"));

        Set<AccessoryModel> models = mapper.toAccessoryEntities(dtos);

        assertEquals(1, models.size());
        assertTrue(models.stream().anyMatch(a -> a.getName().equals("GPS")));
    }

    @Test
    void shouldMapVehicleModelToResponseDTO() {
        VehicleModel model = new VehicleModel();
        model.setModel("Uno");
        model.setYear(2020);
        model.setEngine("1.0");

        VehicleResponseDTO dto = mapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals("Uno 2020 1.0", dto.carTitle());
    }

    @Test
    void shouldMapListOfVehicleModelsToResponseDTOList() {
        VehicleModel model = new VehicleModel();
        model.setModel("Uno");
        model.setYear(2020);
        model.setEngine("1.0");

        List<VehicleResponseDTO> dtos = mapper.toResponseDTOList(List.of(model));

        assertEquals(1, dtos.size());
        assertEquals("Uno 2020 1.0", dtos.getFirst().carTitle());
    }

    @Test
    void shouldLinkVehicleToMaintenancesAfterMapping() {
        MaintenanceModel m1 = new MaintenanceModel();
        MaintenanceModel m2 = new MaintenanceModel();

        VehicleModel vehicle = new VehicleModel();
        vehicle.moveForMaintenance(m1);
        vehicle.moveForMaintenance(m2);

        mapper.linkVehicle(vehicle);

        assertEquals(vehicle, m1.getVehicleModel());
        assertEquals(vehicle, m2.getVehicleModel());
    }

}