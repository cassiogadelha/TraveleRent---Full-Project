package verso.caixa.security.keycloak.authorization;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@RegisterRestClient(configKey = "keycloak-user-api")
public interface KeycloakUserClient {/*

    @POST
    @Path("/admin/realms/travelerent/users")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(@HeaderParam("Authorization") String token, Map<String, Object> user);

    @PUT
    @Path("/admin/realms/travelerent/users/{id}/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    void setPassword(@HeaderParam("Authorization") String token, @PathParam("id") String userId, Map<String, Object> password);
*/}

