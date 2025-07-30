package verso.caixa.dto;

import verso.caixa.enums.BookingStatusEnum;

public record UpdateBookingStatusRequest(
        BookingStatusEnum newStatus
) {
}
