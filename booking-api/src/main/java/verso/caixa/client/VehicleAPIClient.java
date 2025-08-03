package verso.caixa.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;

@RegisterRestClient(configKey = "vehicle-api")
@ApplicationScoped //para os testes
public interface VehicleAPIClient {

    @GET
    @Path("/vehicles/{vehicleId}")
    Vehicle findVehicleById(@PathParam("vehicleId") @NotNull UUID id);

    record Vehicle(String status) {}

}