package verso.caixa.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateMaintenanceRequestDTO;
import verso.caixa.service.MaintenanceService;

import java.util.UUID;

@Path("/api/v1/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaintenanceResource {
    private final MaintenanceService maintenanceService;

    public MaintenanceResource(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @POST
    @Path("/{vehicleId}/maintenances")
    @Transactional
    public Response addMaintenance(@PathParam("vehicleId") UUID vehicleId, CreateMaintenanceRequestDTO request) {

        return maintenanceService.addMaintenance(vehicleId, request);
    }

    @GET
    @Path("/{vehicleId}/maintenances/{maintenanceId}")
    public Response getMaintenanceById(@PathParam("vehicleId") UUID vehicleId, @PathParam("maintenanceId") UUID maintenanceId) {

        return maintenanceService.findById(vehicleId, maintenanceId);

    }
}
