package verso.caixa.helpers;

import verso.caixa.model.BookingModel;

import java.time.LocalDate;
import java.util.UUID;

public class BookingTestHelper {

    public static BookingModel buildValidBooking() {
        return new BookingModel(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Sara Campos",
                UUID.randomUUID()
        );
    }

    public static BookingModel buildCustomBooking(UUID id, LocalDate start, LocalDate end, String name, UUID vehicleId) {
        return new BookingModel(id, start, end, name, vehicleId);
    }
}

