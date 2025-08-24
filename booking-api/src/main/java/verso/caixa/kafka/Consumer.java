package verso.caixa.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import verso.caixa.service.BookingService;

import java.util.UUID;

public class Consumer {

    BookingService bookingService;

    public Consumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Incoming("vehicle-maintenance")
    public void onVehicleMaintenance(UUID vehicleId) {
        bookingService.checkVehicle(vehicleId);
    }
}