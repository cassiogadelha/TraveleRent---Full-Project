package verso.caixa.security.keycloak.authorization;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
public class KeycloakAdminService {

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    @Inject
    @RestClient
    KeycloakUserClient userClient;

    public String getAdminToken() {
        Map<String, String> tokenResponse = tokenClient.getToken(
                "client_credentials",
                "vehicles-backend-service",
                "0CSfjWlFuusccCAd4oSV3AiduukffS6t"
        );
        return tokenResponse.get("access_token");
    }

    public String createUser(String token, String username, String email) {
        Response response = userClient.createUser(
                "Bearer " + token,
                Map.of(
                        "username", username,
                        "email", email,
                        "enabled", true
                )
        );
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf("/") + 1);
    }

    public void setPassword(String token, String userId, String password) {
        userClient.setPassword(
                "Bearer " + token,
                userId,
                Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                )
        );
    }
}

