package verso.caixa.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import verso.caixa.client.WireMockVehicleAPI;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(WireMockVehicleAPI.class)
public class BookingsIntegrationsTest {

    @Test
    public void shouldCreateBookingSuccesfully() {
        UUID vehicleId = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = LocalDate.now().plusDays(5);

        String fakeBooking = """
                {
                    "vehicleId": "%s",
                    "customerName": "Cássio",
                    "startDate": "%s",
                    "endDate": "%s"
                }
            """.formatted(vehicleId, inicio, fim);

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(fakeBooking)
            .when()
            .post("/api/v1/bookings")
            .then()
            .statusCode(201)
            .body("bookingId", notNullValue())
            .body("customerName", is("Cássio"))
            .body("status", is("CREATED"));


    }
}
