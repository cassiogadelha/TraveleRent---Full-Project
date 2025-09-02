package verso.caixa.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ErrorResponseDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.*;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.kafka.BookingEmitterWrapper;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.BookingDAO;
import verso.caixa.repository.VehicleStatusDAO;
import verso.caixa.twilio.SmsService;
import verso.caixa.validations.BookingConflictValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceUnityTests {

    BookingMapper bookingMapper;
    BookingDAO bookingDAO;
    SecurityIdentity securityIdentity;
    SmsService smsService;
    VehicleStatusDAO vehicleStatusDAO;
    VehicleStatusService vehicleStatusService;
    BookingEmitterWrapper bookingEmitterWrapper;

    BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingMapper = mock(BookingMapper.class);
        bookingDAO = mock(BookingDAO.class);
        securityIdentity = mock(SecurityIdentity.class);
        smsService = mock(SmsService.class);
        vehicleStatusDAO = mock(VehicleStatusDAO.class);
        vehicleStatusService = mock(VehicleStatusService.class);
        bookingEmitterWrapper = mock(BookingEmitterWrapper.class);

        bookingService = new BookingService(bookingMapper, bookingDAO, securityIdentity, smsService, vehicleStatusDAO, vehicleStatusService, bookingEmitterWrapper);
    }


    @Test
    void shouldThrowIllegalEndDateExceptionWhenEndDateBeforeStartDate() {
        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().minusDays(1)
        );

        assertThrows(IllegalEndDateException.class, () -> {
            bookingService.createBooking(dto, UUID.randomUUID(), "José Conceição");
        });
    }

    @Test
    void shouldThrowVehicleExceptionWhenVehicleIsNull() {
        when(vehicleStatusDAO.findById(any())).thenReturn(null);

        CreateBookingRequestDTO dto = BookingTestHelper.buildValidBookingDTO();

        assertThrows(VehicleException.class, () -> {
            bookingService.createBooking(dto, UUID.randomUUID(), "Carlos Machado");
        });
    }

    @Test
    void shouldInstantiateErrorResponseDTO() {
        ErrorResponseDTO dto = new ErrorResponseDTO(
                "Erro Interno",
                "Ocorreu uma falha inesperada",
                500,
                "/api/bookings",
                Instant.now(),
                "ERR500"
        );

        assertEquals("Erro Interno", dto.title());
        assertEquals(500, dto.statusCode());
    }


    @Test
    void shouldThrowVehicleExceptionWhenVehicleIsUnavailable() {
        VehicleStatus vehicleStatus = new VehicleStatus(UUID.randomUUID(), "UNDER_MAINTENANCE");
        when(vehicleStatusDAO.findById(any())).thenReturn(vehicleStatus);

        CreateBookingRequestDTO dto = BookingTestHelper.buildValidBookingDTO();

        assertThrows(VehicleException.class, () -> {
            bookingService.createBooking(dto, UUID.randomUUID(), "Gustavo Campos");
        });
    }

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
    void shouldReturn404WhenBookingNotFoundOnUpdate() {
        when(bookingDAO.findById(any())).thenReturn(null);

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.checkBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.FINISHED));
        });
    }

    @Test
    void shouldReturnConflictWhenInvalidStatusChange() {
        BookingModel model = mock(BookingModel.class);

        doThrow(new IllegalBookingStatus("Inválido", ErrorCode.INVALID_STATUS))
                .when(model).setStatus(any());

        when(bookingDAO.findById(any())).thenReturn(model);

        Response response = bookingService.checkBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.FINISHED));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnNoContentWhenStatusUpdatedSuccessfully() {
        BookingModel model = new BookingModel();

        when(bookingDAO.findById(any())).thenReturn(model);

        Response response = bookingService.checkBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED));

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturn404WhenBookingNotFound() {
        when(bookingDAO.findById(any())).thenReturn(null);

        Response response = bookingService.findById(UUID.randomUUID());

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnBookingDTOWhenBookingIsFound() {
        BookingModel booking = BookingTestHelper.buildValidBooking();

        when(bookingDAO.findById(any())).thenReturn(booking);
        when(bookingMapper.toResponseDTO(any())).thenReturn(BookingTestHelper.buildValidResponseBookingDTO());


        Response response = bookingService.findById(UUID.randomUUID());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldReturnMessageWhenBookingListIsEmpty() {
        when(bookingDAO.findAll()).thenReturn(mock(PanacheQuery.class));
        when(bookingDAO.findAll().list()).thenReturn(Collections.emptyList());

        Response response = bookingService.getAllBookings(UUID.randomUUID(), 0, 10, false);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Você não possui nenhum agendamento ainda."));
    }

    @Test
    void shouldReturnBookingListSuccessfully() {
        List<BookingModel> bookings = List.of(BookingTestHelper.buildValidBooking());

        PanacheQuery<BookingModel> queryMock = mock(PanacheQuery.class);
        when(queryMock.page(any())).thenReturn(queryMock);
        when(queryMock.list()).thenReturn(bookings);
        when(bookingDAO.findAll()).thenReturn(queryMock);
        when(bookingMapper.toResponseDTOList(any())).thenReturn(List.of(BookingTestHelper.buildValidResponseBookingDTO()));

        Response response = bookingService.getAllBookings(UUID.randomUUID(), 0, 10, false);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldEmitActivatedBooking() {
        BookingModel booking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(booking.getBookingId())).thenReturn(booking);

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.ACTIVATED);
        bookingService.checkBooking(booking.getBookingId(), dto);

        Mockito.verify(bookingEmitterWrapper).sendActivated(booking);
        Mockito.verify(bookingEmitterWrapper, Mockito.never()).sendCanceled(Mockito.any());
        Mockito.verify(bookingEmitterWrapper, Mockito.never()).sendFinished(Mockito.any());
    }

    @Test
    void shouldEmitActivatedAndFinishedBooking() {
        BookingModel booking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(booking.getBookingId())).thenReturn(booking);

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.ACTIVATED);
        bookingService.checkBooking(booking.getBookingId(), dto);

        dto = new UpdateBookingStatusRequest(BookingStatusEnum.FINISHED);
        bookingService.checkBooking(booking.getBookingId(), dto);

        Mockito.verify(bookingEmitterWrapper).sendActivated(booking);
        Mockito.verify(bookingEmitterWrapper).sendFinished(booking);
        Mockito.verify(bookingEmitterWrapper, Mockito.never()).sendCanceled(Mockito.any());
    }

    @Test
    void shouldEmitCanceledBooking() {
        BookingModel booking = BookingTestHelper.buildValidBooking();

        Mockito.when(bookingDAO.findById(booking.getBookingId())).thenReturn(booking);

        UpdateBookingStatusRequest dto = new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED);
        bookingService.checkBooking(booking.getBookingId(), dto);

        Mockito.verify(bookingEmitterWrapper).sendCanceled(booking);
        Mockito.verify(bookingEmitterWrapper, Mockito.never()).sendActivated(Mockito.any());
        Mockito.verify(bookingEmitterWrapper, Mockito.never()).sendFinished(Mockito.any());
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
        assertTrue(response.getEntity().toString().contains("Erro ao alterar o status do agendamento"), "Mensagem de erro esperada");
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
    void shouldFailValidationWhenConflictExists() {

        BookingConflictValidator validator = new BookingConflictValidator(bookingDAO);

        Mockito.when(bookingDAO.existConflict(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
                UUID.randomUUID(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7));

        boolean result = validator.isValid(dto, null);

        Assertions.assertFalse(result, "A validação deveria falhar por conflito de datas");
    }
}
