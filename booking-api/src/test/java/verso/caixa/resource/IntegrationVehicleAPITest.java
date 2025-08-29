package verso.caixa.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.exception.RemoteServiceException;
import verso.caixa.helpers.BookingTestHelper;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;


@QuarkusTest
public class IntegrationVehicleAPITest {
    @RestClient
    @Inject
    @InjectMock
    VehicleAPIClient vehicleAPIClient;

    /*
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

        CreateBookingRequestDTO fakebooking = BookingTestHelper.buildValidBookingDTO();

        // Chama a API e valida a resposta JSON gerada pelo ExceptionMapper
        given()
                .contentType(ContentType.JSON)
                .body(fakebooking)
                .when()
                .post("/api/v1/bookings")
                .then()
                .statusCode(404)
                .body("title", is("Problema ao procurar o veículo!"))
                .body("details", is("O veículo não existe!"))
                .body("errorCode", is("VEHICLE-001"));
    }

     */
}
