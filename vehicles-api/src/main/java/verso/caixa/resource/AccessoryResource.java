package verso.caixa.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.AddAccessoryRequestDTO;
import verso.caixa.service.AccessoryService;

import java.util.UUID;

@Path("/api/v1/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessoryResource {

    private final AccessoryService accessoryService;

    public AccessoryResource(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @PUT
    @Transactional
    @Path("/{id}/accessories")
    public Response addAccessory(@PathParam("id") UUID id, AddAccessoryRequestDTO dto) {

        return accessoryService.addAccessory(id, dto);

    }
}