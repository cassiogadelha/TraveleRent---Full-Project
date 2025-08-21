package verso.caixa.model;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class TestingKafka {

    @Incoming("activated-bookings")
    public void consume(BookingModel booking){
        System.out.println("UMA RESERVA FOI ATIVADA: " + booking);
    }
}
