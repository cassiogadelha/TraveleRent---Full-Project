package verso.caixa.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.exception.RemoteServiceException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.UUID;


@QuarkusTest
public class IntegrationVehicleAPITest {
    @RestClient
    @Inject
    @InjectMock
    VehicleAPIClient vehicleAPIClient;

    @Test
    public void shouldReturnNotFoundFromVehicleAPI() {
        UUID id = UUID.randomUUID();

        // Simula erro remoto
        when(vehicleAPIClient.findVehicleById(id)).thenThrow(
                new RemoteServiceException(
                        "Veículo não encontrado",
                        "VEICULO_NAO_ENCONTRADO",
                        "O veículo com ID fornecido não existe",
                        "/vehicles/" + id,
                        java.time.Instant.now(),
                        404
                )
        );

        // Chama a API e valida a resposta JSON gerada pelo ExceptionMapper
        given()
                .pathParam("id", id)
                .when()
                .get("/consulta/{id}")
                .then()
                .statusCode(404)
                .body("message", is("Veículo não encontrado"))
                .body("errorCode", is("VEICULO_NAO_ENCONTRADO"))
                .body("httpStatus", is(404));
    }

}
