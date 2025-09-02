package verso.caixa.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorCodeTest {
    @Test
    void shouldReturnCorrectCodeForInvalidEndDate() {
        assertEquals("DATE-001", ErrorCode.INVALID_END_DATE.code());
    }

    @Test
    void shouldReturnCorrectCodeForNullVehicle() {
        assertEquals("VEHICLE-001", ErrorCode.NULL_VEHICLE.code());
    }

    @Test
    void shouldReturnCorrectCodeForNullBooking() {
        assertEquals("BOOKING-001", ErrorCode.NULL_BOOKING.code());
    }

    @Test
    void shouldReturnCorrectCodeForUnavailableVehicle() {
        assertEquals("VEHICLE-002", ErrorCode.UNAVAILABLE_VEHICLE.code());
    }

    @Test
    void shouldReturnCorrectCodeForInvalidStatus() {
        assertEquals("STATUS-001", ErrorCode.INVALID_STATUS.code());
    }

}
