package verso.caixa.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.VehicleStatusDAO;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class VehicleStatusServiceUnityTests {

    private VehicleStatusDAO vehicleStatusDAO;
    private VehicleStatusService service;

    @BeforeEach
    void setup() {
        vehicleStatusDAO = mock(VehicleStatusDAO.class);
        service = new VehicleStatusService(vehicleStatusDAO);
    }

    @Test
    void shouldAddVehicleStatus() {
        UUID vehicleId = UUID.randomUUID();
        VehicleProducerDTO dto = new VehicleProducerDTO(vehicleId, "AVAILABLE");

        service.addVehicle(dto);

        verify(vehicleStatusDAO, times(1)).persist(any(VehicleStatus.class));
    }

    @Test
    void shouldReturnVehicleStatusList() {
        VehicleStatus status = new VehicleStatus(UUID.randomUUID(), "UNDER_MAINTENANCE");

        PanacheQuery<VehicleStatus> queryMock = mock(PanacheQuery.class);

        when(vehicleStatusDAO.findAll()).thenReturn(queryMock);
        when(queryMock.page(any())).thenReturn(queryMock);
        when(queryMock.list()).thenReturn(List.of(status));

        Response response = service.getVehicleStatus(0, 10);

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof List);
        assertEquals(1, ((List<?>) response.getEntity()).size());
    }

    @Test
    void shouldNotChangeVehicleStatusWhenNotFound() {
        UUID vehicleId = UUID.randomUUID();
        VehicleProducerDTO dto = new VehicleProducerDTO(vehicleId, "MANUTENCAO");

        when(vehicleStatusDAO.findById(vehicleId)).thenReturn(null);

        service.changeVehicleStatus(dto);

        verify(vehicleStatusDAO, never()).persist(any(VehicleStatus.class));
    }

}
