package verso.caixa.helpers;

import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.model.BookingModel;

import java.time.LocalDate;
import java.util.UUID;

public class BookingTestHelper {

    public static BookingModel buildValidBooking() {
        return new BookingModel(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    public static CreateBookingRequestDTO buildValidBookingDTO() {
        return new CreateBookingRequestDTO(
                UUID.randomUUID(),
                "Vivi Araújo",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
    }

    public static BookingModel buildCustomBooking(UUID vehicleId, UUID customerId, LocalDate start, LocalDate end) {
        return new BookingModel(UUID.randomUUID(), start, end, customerId, vehicleId);
    }

    public static CreateBookingRequestDTO buildCustomBookingDTO(String name, LocalDate start, LocalDate end) {
        return new CreateBookingRequestDTO(UUID.randomUUID(), name, start, end);
    }

    public static ResponseBookingDTO buildValidResponseBookingDTO(){
        return new ResponseBookingDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Márcio Santos",
                LocalDate.now(),
                LocalDate.now(),
                BookingStatusEnum.CREATED);
    }
}

