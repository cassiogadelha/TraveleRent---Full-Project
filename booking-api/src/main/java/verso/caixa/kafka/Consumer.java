package verso.caixa.kafka;

import io.quarkus.logging.Log;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import verso.caixa.service.BookingService;
import verso.caixa.service.VehicleStatusService;

import java.util.UUID;

public class Consumer {

    private final BookingService bookingService;
    private final VehicleStatusService vehicleStatusService;

    public Consumer(BookingService bookingService, VehicleStatusService vehicleStatusService) {
        this.bookingService = bookingService;
        this.vehicleStatusService = vehicleStatusService;
    }

    @Incoming("vehicle-status-changed")
    public void onVehicleStatusChanged(VehicleProducerDTO dto) {
        Log.info("RECEIVED VEHICLE STATUS CHANGED: " + dto);
        bookingService.checkVehicle(dto);
    }

    @Incoming("vehicle-created")
    public void onVehicleCreated(VehicleProducerDTO dto) { vehicleStatusService.addVehicle(dto); }
}