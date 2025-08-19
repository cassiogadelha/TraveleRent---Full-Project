package verso.caixa.client;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/v1/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

    @Inject
    KeycloakAdminService keycloakAdminService;

    @POST
    public Response register() {
        String token = keycloakAdminService.getAdminToken();

        return Response.status(Response.Status.CREATED).entity(token).build();
    }
}
