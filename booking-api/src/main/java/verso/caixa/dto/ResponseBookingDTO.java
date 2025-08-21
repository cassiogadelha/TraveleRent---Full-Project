package verso.caixa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import verso.caixa.enums.BookingStatusEnum;

import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseBookingDTO(
        UUID bookingId,
        UUID vehicleId,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatusEnum status,
        LocalDate activatedAt,
        LocalDate finishedAt,
        LocalDate canceledAt
) {
}
