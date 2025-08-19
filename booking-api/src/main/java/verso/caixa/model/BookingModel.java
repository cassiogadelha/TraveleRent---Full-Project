package verso.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.IllegalBookingStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity //ORM PANACHE no pom
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_booking")
public class BookingModel extends PanacheEntityBase {
    private static final Map<BookingStatusEnum, Set<BookingStatusEnum>> BOOKING_STATUS = new HashMap<>() {
    };

    static {
        BOOKING_STATUS.put(BookingStatusEnum.CREATED, Set.of(BookingStatusEnum.CANCELED, BookingStatusEnum.FINISHED));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID bookingId;

    public UUID vehicleId;

    private UUID customerId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status = BookingStatusEnum.CREATED;

    public BookingModel(UUID vehicleId, UUID customerId, LocalDate startDate, LocalDate endDate) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public BookingModel(UUID id, LocalDate startDate, LocalDate endDate, UUID customerId, UUID vehicleId) {
        this.bookingId = id;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setStatus(BookingStatusEnum incomingStatus) {

        Set<BookingStatusEnum> possibleStatus = BOOKING_STATUS.get(this.status);

        if (possibleStatus == null || !possibleStatus.contains(incomingStatus)) {
            throw new IllegalBookingStatus("Não é possível mudar para esse status. Status válidos são: " + possibleStatus, ErrorCode.INVALID_STATUS);
        }

        if (incomingStatus.equals(this.status)) {
            return;
        }

        this.status = incomingStatus;

    }

}
