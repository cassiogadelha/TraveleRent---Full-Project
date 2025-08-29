package verso.caixa.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import verso.caixa.client.WireMockVehicleAPI;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.helpers.BookingTestHelper;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.*;


@QuarkusTest
@QuarkusTestResource(WireMockVehicleAPI.class)
public class BookingsIntegrationsTest {

    private String getAdminAccessToken() {
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "bookings-backend-service")
                .formParam("username", "admin")
                .formParam("password", "admin")
                .formParam("grant_type", "password")
                .formParam("client_secret", "cuv0nz1enzpp8aTLruUOLthU6NEyU0vs")
                .when()
                .post("http://localhost:8888/realms/travelerent/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }


   /*
    @Test
    public void shouldCreateBookingSuccesfully() {
        UUID vehicleId = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fim = LocalDate.now().plusDays(5);

        String fakeBooking = """
                {
                    "vehicleId": "%s",
                    "startDate": "%s",
                    "endDate": "%s"
                }
            """.formatted(vehicleId, inicio, fim);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + getAdminAccessToken())
            .body(fakeBooking)
            .when()
            .post("/api/v1/bookings")
            .then()
            .statusCode(201)
            .body("bookingId", notNullValue())
            .body("customerName", is("admin"))
            .body("status", is("CREATED"));
    }

    */

    @Test
    void shouldReturnBadRequestForBookingWithInvalidStartDate() throws JsonProcessingException {

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(3));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getAdminAccessToken()) // simula token válido
                .body(dto)
                .when()
                .post("/api/v1/bookings")
                .then()
                .statusCode(400)
                .body("violations", not(empty()))
                .body("violations.message", hasItem("A data de início não pode ser anterior a hoje."));
    }
}
