package verso.caixa.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ErrorResponseDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.exception.RemoteServiceException;
import verso.caixa.exception.RemoteServiceExceptionMapper;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.repository.BookingDAO;
import verso.caixa.service.BookingService;
import verso.caixa.validations.BookingConflictValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ResourceIntegrationTest {

    @Inject
    BookingConflictValidator validator;

    @InjectMock
    BookingDAO bookingDAO;

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingDAO bookingDAO = Mockito.mock(BookingDAO.class);
        VehicleAPIClient vehicleAPIClient = Mockito.mock(VehicleAPIClient.class);
        BookingMapper bookingMapper = Mockito.mock(BookingMapper.class);

        Mockito.when(vehicleAPIClient.findVehicleById(Mockito.any(UUID.class))).thenReturn(new VehicleAPIClient.Vehicle(
                "AVAILABLE"
        ));

        BookingService bookingService = new BookingService(bookingMapper, bookingDAO, vehicleAPIClient);

        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(UUID.randomUUID(),
                "Sara Campos",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3));

        Mockito.when(bookingMapper.toEntity(dto)).thenReturn(fakeBooking);

        Assertions.assertDoesNotThrow(() -> {
            bookingService.createBooking(dto);
        });
    }

    @Test
    void shouldReturnBadRequestForBookingWithInvalidStartDate() throws JsonProcessingException {

        CreateBookingRequestDTO bookingDto = new CreateBookingRequestDTO(UUID.randomUUID(),
                "Sara Campos",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(3));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String bookingJson = mapper.writeValueAsString(bookingDto);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/api/v1/bookings")
                .then()
                .statusCode(400)
                .body("violations[0].message", equalTo("A data de início não pode ser anterior a hoje."));
    }

    @Test
    void shouldFailValidationWhenConflictExists() {
        // Simula reserva existente que entra em conflito
        Mockito.when(bookingDAO.existConflict(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        // DTO com período que vai conflitar
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                UUID.randomUUID(),
                "Laura Teste",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7)
        );

        boolean result = validator.isValid(dto, null); // context pode ser nulo aqui

        Assertions.assertFalse(result, "A validação deveria falhar por conflito de datas");
    }

    @Test
    void shouldCancelBookingSuccessfully_WhenStatusIsValid() {

        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(fakeBooking.getBookingId())).thenReturn(fakeBooking);

        BookingService bookingService = new BookingService(
                Mockito.mock(BookingMapper.class), bookingDAO, Mockito.mock(VehicleAPIClient.class)
        );

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED);

        // Act
        Response response = bookingService.updateBooking(fakeBooking.getBookingId(), dto);

        // Assert
        assertEquals(204, response.getStatus(), "Cancelamento deveria retornar 204");
        assertEquals(BookingStatusEnum.CANCELED, fakeBooking.getStatus(), "Status deveria ter sido alterado");
    }

    @Test
    void shouldReturnConflictWhenStatusTransitionIsInvalid() {
        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(fakeBooking.getBookingId())).thenReturn(fakeBooking);

        BookingService bookingService = new BookingService(
                Mockito.mock(BookingMapper.class), bookingDAO, Mockito.mock(VehicleAPIClient.class)
        );

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED);

        Response response = bookingService.updateBooking(fakeBooking.getBookingId(), dto);

        assertEquals(204, response.getStatus(), "Cancelamento deveria retornar 204");
        assertEquals(BookingStatusEnum.CANCELED, fakeBooking.getStatus(), "Status deveria ter sido alterado");

        dto = new UpdateBookingStatusRequest(BookingStatusEnum.CREATED);

        response = bookingService.updateBooking(fakeBooking.getBookingId(), dto);

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
