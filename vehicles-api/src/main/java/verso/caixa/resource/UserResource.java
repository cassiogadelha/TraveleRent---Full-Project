package verso.caixa.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateUserDTO;
import verso.caixa.model.UserModel;
import verso.caixa.service.UserService;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @RolesAllowed("Admin")
    @Transactional
    public Response createUser(CreateUserDTO dto) {
        return userService.findByUsername(dto);
    }
}
