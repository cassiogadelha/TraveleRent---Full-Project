package verso.caixa.dto;

import verso.caixa.enums.BookingStatusEnum;

import java.time.LocalDate;
import java.util.UUID;

public record ResponseBookingDTO(
        UUID bookingId,
        UUID vehicleId,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatusEnum status
) {
}
