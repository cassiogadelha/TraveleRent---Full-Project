package verso.caixa.model;

import org.junit.jupiter.api.Test;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.IllegalBookingStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BookingModelTest {

    @Test
    void shouldUseConstructorWithVehicleAndCustomerId() {
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = start.plusDays(5);

        BookingModel booking = new BookingModel(vehicleId, customerId, start, end);

        assertNotNull(booking); // força uso do construtor
        assertEquals(vehicleId, booking.getVehicleId());
        assertEquals(customerId, booking.getCustomerId());
    }

    @Test
    void shouldCreateBookingWithConstructor1() {
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);

        BookingModel booking = new BookingModel(vehicleId, customerId, start, end);

        assertEquals(vehicleId, booking.getVehicleId());
        assertEquals(customerId, booking.getCustomerId());
        assertEquals(start, booking.getStartDate());
        assertEquals(end, booking.getEndDate());
    }

    @Test
    void shouldCreateBookingWithConstructor2() {
        UUID bookingId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);

        BookingModel booking = new BookingModel(bookingId, start, end, customerId, vehicleId);

        assertEquals(bookingId, booking.getBookingId());
        assertEquals(vehicleId, booking.getVehicleId());
        assertEquals(customerId, booking.getCustomerId());
        assertEquals(start, booking.getStartDate());
        assertEquals(end, booking.getEndDate());
    }

    @Test
    void shouldCreateBookingWithConstructor3() {
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);

        BookingModel booking = new BookingModel(start, end, customerId, vehicleId);

        assertEquals(vehicleId, booking.getVehicleId());
        assertEquals(customerId, booking.getCustomerId());
        assertEquals(start, booking.getStartDate());
        assertEquals(end, booking.getEndDate());
    }

    @Test
    void shouldThrowExceptionWhenStatusTransitionIsInvalid() {
        BookingModel booking = new BookingModel();

        IllegalBookingStatus exception = assertThrows(IllegalBookingStatus.class, () ->
                booking.setStatus(BookingStatusEnum.FINISHED)
        );

        assertEquals(ErrorCode.INVALID_STATUS.code(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Erro ao alterar o status do agendamento"));
    }

    @Test
    void shouldReturnEarlyWhenStatusIsSame() {
        BookingModel booking = new BookingModel();
        booking.setStatus(BookingStatusEnum.CREATED);
        booking.setStatus(BookingStatusEnum.CREATED);
        assertEquals(BookingStatusEnum.CREATED, booking.getStatus());
    }

    @Test
    void shouldChangeStatusFromCreatedToActivated() {
        BookingModel booking = new BookingModel();

        booking.setStatus(BookingStatusEnum.ACTIVATED);

        assertEquals(BookingStatusEnum.ACTIVATED, booking.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCurrentStatusHasNoTransitions() {
        BookingModel booking = new BookingModel();
        booking.setStatus(BookingStatusEnum.CANCELED);

        IllegalBookingStatus exception = assertThrows(IllegalBookingStatus.class, () ->
                booking.setStatus(BookingStatusEnum.CREATED)
        );

        assertEquals(ErrorCode.INVALID_STATUS.code(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Não é possível alterar status de um agendamento finalizado ou cancelado."));
    }

    @Test
    void shouldThrowExceptionWhenCurrentStatusHasNoTransitions_definedAsFinished() {
        BookingModel booking = new BookingModel();
        booking.setStatus(BookingStatusEnum.CANCELED);

        IllegalBookingStatus exception = assertThrows(IllegalBookingStatus.class, () ->
                booking.setStatus(BookingStatusEnum.CREATED) // tentativa inválida
        );

        assertTrue(exception.getMessage().contains("Não é possível alterar status de um agendamento finalizado ou cancelado."));
    }

    @Test
    void shouldThrowExceptionWhenIncomingStatusIsNotAllowed() {
        BookingModel booking = new BookingModel();
        booking.setStatus(BookingStatusEnum.CREATED);

        IllegalBookingStatus exception = assertThrows(IllegalBookingStatus.class, () ->
                booking.setStatus(BookingStatusEnum.FINISHED)
        );

        assertTrue(exception.getMessage().contains("É possível alterar o agendamento somente para o(s) statu(s):"));
    }

}
