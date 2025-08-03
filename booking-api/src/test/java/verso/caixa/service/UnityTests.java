package verso.caixa.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.IllegalBookingStatus;
import verso.caixa.exception.IllegalEndDateException;
import verso.caixa.exception.VehicleException;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.repository.BookingDAO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UnityTests {

    BookingMapper bookingMapper;
    BookingDAO bookingDAO;
    VehicleAPIClient vehicleAPIClient;

    BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingMapper = mock(BookingMapper.class);
        bookingDAO = mock(BookingDAO.class);
        vehicleAPIClient = mock(VehicleAPIClient.class);

        bookingService = new BookingService(bookingMapper, bookingDAO, vehicleAPIClient);
    }


    @Test
    void shouldThrowIllegalEndDateExceptionWhenEndDateBeforeStartDate() {
        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
            "João",
            LocalDate.now(),
            LocalDate.now().minusDays(1)
        );

        assertThrows(IllegalEndDateException.class, () -> {
            bookingService.createBooking(dto);
        });
    }

    @Test
    void shouldThrowVehicleExceptionWhenVehicleIsNull() {
        when(vehicleAPIClient.findVehicleById(any())).thenReturn(null);

        CreateBookingRequestDTO dto = BookingTestHelper.buildValidBookingDTO();

        assertThrows(VehicleException.class, () -> {
            bookingService.createBooking(dto);
        });
    }

    @Test
    void shouldThrowVehicleExceptionWhenVehicleIsUnavailable() {
        VehicleAPIClient.Vehicle mockVehicle = new VehicleAPIClient.Vehicle("RENTED");
        when(vehicleAPIClient.findVehicleById(any())).thenReturn(mockVehicle);

        CreateBookingRequestDTO dto = BookingTestHelper.buildValidBookingDTO();

        assertThrows(VehicleException.class, () -> {
            bookingService.createBooking(dto);
        });
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        VehicleAPIClient.Vehicle mockVehicle = new VehicleAPIClient.Vehicle("AVAILABLE");
        when(vehicleAPIClient.findVehicleById(any())).thenReturn(mockVehicle);

        BookingModel bookingModel = mock(BookingModel.class);
        when(bookingMapper.toEntity(any())).thenReturn(bookingModel);

        CreateBookingRequestDTO dto = BookingTestHelper.buildValidBookingDTO();

        Response response = bookingService.createBooking(dto);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(bookingModel, response.getEntity());
    }

    @Test
    void shouldReturn404WhenBookingNotFoundOnUpdate() {
        when(bookingDAO.findById(any())).thenReturn(null);

        Response response = bookingService.updateBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.FINISHED));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnConflictWhenInvalidStatusChange() {
        BookingModel model = mock(BookingModel.class);
        doThrow(new IllegalBookingStatus("Inválido", ErrorCode.INVALID_STATUS))
                .when(model).setStatus(any());

        when(bookingDAO.findById(any())).thenReturn(model);

        Response response = bookingService.updateBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.FINISHED));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void shouldReturnNoContentWhenStatusUpdatedSuccessfully() {
        BookingModel model = new BookingModel();

        when(bookingDAO.findById(any())).thenReturn(model);

        Response response = bookingService.updateBooking(UUID.randomUUID(), new UpdateBookingStatusRequest(BookingStatusEnum.CANCELED));

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

        Response response = bookingService.getBookingList(0, 10);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("lista de agendamentos está vazia"));
    }

    @Test
    void shouldReturnBookingListSuccessfully() {
        List<BookingModel> bookings = List.of(BookingTestHelper.buildValidBooking());

        PanacheQuery<BookingModel> queryMock = mock(PanacheQuery.class);
        when(queryMock.page(any())).thenReturn(queryMock);
        when(queryMock.list()).thenReturn(bookings);
        when(bookingDAO.findAll()).thenReturn(queryMock);
        when(bookingMapper.toResponseDTOList(any())).thenReturn(List.of(BookingTestHelper.buildValidResponseBookingDTO()));

        Response response = bookingService.getBookingList(0, 10);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }


}
