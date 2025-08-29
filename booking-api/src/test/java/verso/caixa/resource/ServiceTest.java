package verso.caixa.resource;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ErrorResponseDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.exception.RemoteServiceException;
import verso.caixa.exception.RemoteServiceExceptionMapper;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.BookingDAO;
import verso.caixa.repository.VehicleStatusDAO;
import verso.caixa.service.BookingService;
import verso.caixa.service.VehicleStatusService;
import verso.caixa.twilio.SmsService;
import verso.caixa.validations.BookingConflictValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ServiceTest {

    @Inject
    BookingConflictValidator validator;

    @InjectMock
    BookingDAO bookingDAO;

    @InjectMock
    SecurityIdentity securityIdentity;

    @InjectMock
    SmsService smsService;

    @InjectMock
    VehicleStatusService vehicleStatusService;

    @InjectMock
    VehicleStatusDAO vehicleStatusDAO;

    @InjectMock
    BookingMapper bookingMapper;

    @InjectMock BookingService bookingService;

    @Test
    void shouldCreateBookingSuccessfully() {

        UUID vehicleId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(3);
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(vehicleId, startDate, endDate);

        VehicleStatus mockStatus = new VehicleStatus(vehicleId, "AVAILABLE");
        Mockito.when(vehicleStatusDAO.findById(vehicleId)).thenReturn(mockStatus);

        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();
        Mockito.when(bookingMapper.toEntity(dto)).thenReturn(fakeBooking);

        Assertions.assertDoesNotThrow(() -> {
            bookingService.createBooking(dto, UUID.randomUUID(), "Jorge Souza");
        });

    }

    @Test
    void shouldFailValidationWhenConflictExists() {

        Mockito.when(bookingDAO.existConflict(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7));

        boolean result = validator.isValid(dto, null);

        Assertions.assertFalse(result, "A validação deveria falhar por conflito de datas");
    }

    @Test
    void shouldCancelBookingSuccessfully_WhenStatusIsValid() {

        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(fakeBooking.getBookingId())).thenReturn(fakeBooking);

        UpdateBookingStatusRequest dtoStatus = new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED);

        Response response = bookingService.checkBooking(fakeBooking.getBookingId(), dtoStatus);

        assertEquals(204, response.getStatus(), "Cancelamento deve retornar 204");
        assertEquals(BookingStatusEnum.CANCELED, fakeBooking.getStatus(), "Status deveria ter sido alterado");
    }

    @Test
    void shouldReturnConflictWhenStatusTransitionIsInvalid() {
        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(fakeBooking.getBookingId())).thenReturn(fakeBooking);

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED);

        Response response = bookingService.checkBooking(fakeBooking.getBookingId(), dto);

        assertEquals(204, response.getStatus(), "Cancelamento deveria retornar 204");
        assertEquals(BookingStatusEnum.CANCELED, fakeBooking.getStatus(), "Status deveria ter sido alterado");

        dto = new UpdateBookingStatusRequest(BookingStatusEnum.CREATED);

        response = bookingService.checkBooking(fakeBooking.getBookingId(), dto);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Status válidos"), "Mensagem de erro esperada");
    }

    @Test
    public void shouldReturnResponseDTO() {
        // Simula a exceção
        RemoteServiceException ex = new RemoteServiceException(
                "Veículo não encontrado",
                "VEICULO_404",
                "O ID fornecido não corresponde a nenhum veículo",
                "/api/veiculos/999",
                Instant.parse("2025-08-03T12:48:47.263380500Z"),
                404
        );

        // Invoca o mapper
        RemoteServiceExceptionMapper mapper = new RemoteServiceExceptionMapper();
        Response response = mapper.toResponse(ex);

        // Valida o status
        assertEquals(404, response.getStatus());

        // Valida o corpo como DTO
        assertTrue(response.getEntity() instanceof ErrorResponseDTO);
        ErrorResponseDTO dto = (ErrorResponseDTO) response.getEntity();

        assertEquals("Veículo não encontrado", dto.title());
        assertEquals("VEICULO_404", dto.errorCode());
        assertEquals("O ID fornecido não corresponde a nenhum veículo", dto.details());
        assertEquals("/api/veiculos/999", dto.path());
        assertEquals(Instant.parse("2025-08-03T12:48:47.263380500Z"), dto.timestamp());
        assertEquals(404, dto.statusCode());
    }

}
