package verso.caixa.security.keycloak.authorization;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.UserDTO;

import java.util.Map;

@Path("api/v1/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {/*

    @Inject
    KeycloakAdminService keycloakAdminService;

    @POST
    public Response register(UserDTO dto) {
        String token = keycloakAdminService.getAdminToken();
        String userId = keycloakAdminService.createUser(token, dto.username(), dto.email());
        keycloakAdminService.setPassword(token, userId, dto.password());

        return Response.status(Response.Status.CREATED).entity(Map.of("userId", userId)).build();
    }*/
}
