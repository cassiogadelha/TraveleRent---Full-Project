package verso.caixa.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import verso.caixa.dto.UpdateVehicleStatusRequestDTO;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.security.jwt.ActualUser;
import verso.caixa.service.VehicleService;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@Path("/api/v1/vehicles")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    private final VehicleService vehicleService;

    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Inject
    JsonWebToken jwt;

    @Inject
    ActualUser actualUser;

    @POST
    @Transactional
    @RolesAllowed({"Admin", "Employee"})
    public Response createVehicle(@Valid CreateVehicleRequestDTO dto, @Context SecurityContext ctx){
        return vehicleService.createVehicle(dto);
    }

    //  =-=-=-=-=-=-=-=-[TESTE]=-=-=-=-=-=-=-=-=-=-
    @RolesAllowed("Admin")
    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String adminOnly(@Context SecurityContext ctx) {

        //return jwt.getClaimNames();
        return String.format("UPN: %s\nEmail: %s\nNascimento: %s\nGrupos: %s",
                actualUser.getUpn(),
                actualUser.getEmail(),
                actualUser.getBirthdate(),
                String.join(", ", actualUser.getGroups()));
    }

    @GET
    public Response findAllVehicles(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size){
        return vehicleService.getVehicleList(page, size);
    }

    @GET
    @Path("{vehicleId}")
    public Response findById(@PathParam("vehicleId") UUID vehicleId){
        return vehicleService.findById(vehicleId);
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Response updateVehiclePartially(@PathParam("id") UUID vehicleId, UpdateVehicleStatusRequestDTO dto){
        return vehicleService.updateVehicle(vehicleId, dto);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteById(@PathParam("id") UUID vehicleId){
        vehicleService.deleteById(vehicleId);
        return Response.noContent().build();
    }
}

