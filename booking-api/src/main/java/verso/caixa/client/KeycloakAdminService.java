package verso.caixa.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
public class KeycloakAdminService {

    /*@Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    public String getAdminToken() {
        Map<String, String> tokenResponse = tokenClient.getToken(
                "client_credentials",
                "bookings-backend-service",
                "cuv0nz1enzpp8aTLruUOLthU6NEyU0vs"
        );
        return tokenResponse.get("access_token");
    }*/
}


