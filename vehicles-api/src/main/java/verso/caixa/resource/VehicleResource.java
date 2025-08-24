package verso.caixa.resource;

import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import verso.caixa.dto.CreateVehicleRequestDTO;
import verso.caixa.dto.UpdateVehicleStatusRequestDTO;
import verso.caixa.service.VehicleService;

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

    @POST
    @Transactional
    @RolesAllowed({"realm-admin"})
    public Response createVehicle(@Valid CreateVehicleRequestDTO dto, @Context SecurityContext ctx){
        System.out.println(ctx.getUserPrincipal().getName());
        return vehicleService.createVehicle(dto);
    }

    @GET
    public Response findAllVehicles(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size){
        return vehicleService.getVehicleList(page, size);
    }

    @GET
    @Path("{vehicleId}")
    public Response findById(@PathParam("vehicleId") UUID vehicleId){
        Log.info("BOOKING AQUI");
        return vehicleService.findById(vehicleId);
    }

    @PATCH
    @Path("{id}")
    @Transactional
    @RolesAllowed({"realm-admin"})
    public Response updateVehiclePartially(@PathParam("id") UUID vehicleId, UpdateVehicleStatusRequestDTO dto){
        return vehicleService.updateVehicle(vehicleId, dto);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @RolesAllowed({"realm-admin"})
    public Response deleteById(@PathParam("id") UUID vehicleId){
        vehicleService.deleteById(vehicleId);
        return Response.noContent().build();
    }
}

