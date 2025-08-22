package verso.caixa.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SmsService {

    Dotenv dotenv = Dotenv.load();
    String accountSid = dotenv.get("TWILIO_ACCOUNT_SID");
    String authToken = dotenv.get("TWILIO_AUTH_TOKEN");
    String fromNumber = dotenv.get("+18482258954");

    @PostConstruct
    void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

>>>>>>> 3f21ce5 (twilio credentials .env)
    public void sendCancellationNotice(String toNumber) {
        Message.creator(
                new PhoneNumber(toNumber),
                new PhoneNumber(fromNumber),
                "RESERVA CANCELADA! CAPOTARO O CORSA E AGORA ELE TÁ EM MANUTENÇÃO."
        ).create();
    }
}