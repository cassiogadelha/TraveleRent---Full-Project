package verso.caixa.enums;

import verso.caixa.model.BookingModel;

import java.time.LocalDate;

public enum BookingStatusEnum {

    CREATED,
    ACTIVATED,
    CANCELED,
    FINISHED;

    public void applyDate(BookingModel booking) {
        LocalDate now = LocalDate.now();

        switch (this) {
            case CANCELED -> booking.setCanceledAt(now);
            case ACTIVATED -> booking.setActivatedAt(now);
            case FINISHED -> booking.setFinishedAt(now);
        }
    }
}