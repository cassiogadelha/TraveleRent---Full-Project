package verso.caixa.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import verso.caixa.model.BookingModel;

@ApplicationScoped
public class BookingEmitterWrapper {

    @Inject
    @Channel("booking-in")
    Emitter<BookingModel> activatedEmitter;

    @Inject
    @Channel("booking-out")
    Emitter<BookingModel> finishedEmitter;

    @Inject
    @Channel("booking-cancel")
    Emitter<BookingModel> canceledEmitter;

    public void sendActivated(BookingModel booking) {
        activatedEmitter.send(booking);
    }

    public void sendFinished(BookingModel booking) {
        finishedEmitter.send(booking);
    }

    public void sendCanceled(BookingModel booking) {
        canceledEmitter.send(booking);
    }
}
