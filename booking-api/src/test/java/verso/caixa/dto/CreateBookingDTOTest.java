package verso.caixa.dto;

import java.time.LocalDate;

public record CreateBookingDTOTest(
    String customerName,
    String vehicleId,
    LocalDate startDate,
    LocalDate endDate
) {
}
