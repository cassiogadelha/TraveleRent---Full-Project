package verso.caixa.exception;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.enums.ErrorCode;
import verso.caixa.helpers.BookingTestHelper;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class VehicleExceptionTest {

    @Test
    void shouldReturnErrorResponseDTOWhenVehicleIsNull() {
        String employeeToken = BookingTestHelper.getEmployeeAccessToken();

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
            UUID.randomUUID(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        );

        given()
            .header("Authorization", "Bearer " + employeeToken)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/v1/bookings?customerId=" + UUID.randomUUID() + "&customerName=Cássio")
            .then()
            .statusCode(404)
            .body("title", equalTo("Problema ao procurar o veículo!"))
            .body("details", equalTo("O veículo não existe!"))
            .body("errorCode", equalTo(ErrorCode.NULL_VEHICLE.code()))
            .body("path", containsString("/api/v1/bookings"))
            .body("timestamp", notNullValue());
    }
}
