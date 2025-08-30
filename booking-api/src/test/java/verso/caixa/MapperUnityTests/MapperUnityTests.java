package verso.caixa.MapperUnityTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MapperUnityTests {
    private BookingMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    void shouldMapCreateBookingRequestDTOToEntity() {
        UUID vehicleId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(vehicleId, start, end);

        BookingModel entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(vehicleId, entity.getVehicleId());
        assertEquals(start, entity.getStartDate());
        assertEquals(end, entity.getEndDate());
    }

    @Test
    void shouldMapBookingModelToResponseDTO() {
        BookingModel model = new BookingModel();
        model.setBookingId(UUID.randomUUID());
        model.setVehicleId(UUID.randomUUID());
        model.setCustomerName("Ana Souza");
        model.setStartDate(LocalDate.now());
        model.setEndDate(LocalDate.now().plusDays(2));

        ResponseBookingDTO dto = mapper.toResponseDTO(model);

        assertNotNull(dto);
        assertEquals(model.getBookingId(), dto.bookingId());
        assertEquals(model.getVehicleId(), dto.vehicleId());
        assertEquals(model.getCustomerName(), dto.customerName());
        assertEquals(model.getStartDate(), dto.startDate());
        assertEquals(model.getEndDate(), dto.endDate());
        assertEquals(model.getStatus(), dto.status());
    }

    @Test
    void shouldMapListOfBookingModelsToResponseDTOList() {
        BookingModel model1 = new BookingModel();
        model1.setBookingId(UUID.randomUUID());
        model1.setCustomerName("Carlos Andrade");
        model1.setStartDate(LocalDate.now());
        model1.setEndDate(LocalDate.now().plusDays(1));

        BookingModel model2 = new BookingModel();
        model2.setBookingId(UUID.randomUUID());
        model2.setCustomerName("Ana Souza");
        model2.setStartDate(LocalDate.now().plusDays(2));
        model2.setEndDate(LocalDate.now().plusDays(4));
        model2.setStatus(BookingStatusEnum.ACTIVATED);

        List<ResponseBookingDTO> dtos = mapper.toResponseDTOList(List.of(model1, model2));

        assertEquals(2, dtos.size());
        assertEquals("Carlos Andrade", dtos.get(0).customerName());
        assertEquals("Ana Souza", dtos.get(1).customerName());
    }

}
