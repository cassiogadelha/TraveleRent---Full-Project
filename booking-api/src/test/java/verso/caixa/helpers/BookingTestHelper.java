package verso.caixa.helpers;

import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.model.BookingModel;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class BookingTestHelper {

    public static BookingModel buildValidBooking() {
        return new BookingModel(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    public static BookingModel buildValidBookingWithVehicleId(UUID vehicleId) {
        return new BookingModel(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                UUID.randomUUID(),
                vehicleId
        );
    }

    public static CreateBookingRequestDTO buildValidBookingDTO() {
        return new CreateBookingRequestDTO(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
    }

    public static BookingModel buildCustomBooking(UUID vehicleId, UUID customerId, LocalDate start, LocalDate end) {
        return new BookingModel(UUID.randomUUID(), start, end, customerId, vehicleId);
    }

    public static CreateBookingRequestDTO buildCustomBookingDTO(UUID vehicleId, LocalDate start, LocalDate end) {
        return new CreateBookingRequestDTO(vehicleId, start, end);
    }

    public static ResponseBookingDTO buildValidResponseBookingDTO(){
        return new ResponseBookingDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Márcio Santos",
                LocalDate.now(),
                LocalDate.now(),
                BookingStatusEnum.CREATED,
                null,
                null,
                null);

    }

    public static String getAdminAccessToken() {
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

    public static String getAnaAccessToken() {
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "bookings-backend-service")
                .formParam("username", "anasouza0")
                .formParam("password", "ana0")
                .formParam("grant_type", "password")
                .formParam("client_secret", "cuv0nz1enzpp8aTLruUOLthU6NEyU0vs")
                .when()
                .post("http://localhost:8888/realms/travelerent/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

    public static String getCarlosAccessToken() {
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "bookings-backend-service")
                .formParam("username", "carlosandrade")
                .formParam("password", "carlos0")
                .formParam("grant_type", "password")
                .formParam("client_secret", "cuv0nz1enzpp8aTLruUOLthU6NEyU0vs")
                .when()
                .post("http://localhost:8888/realms/travelerent/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

    public static String getEmployeeAccessToken() {
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "bookings-backend-service")
                .formParam("username", "employee")
                .formParam("password", "employee0")
                .formParam("grant_type", "password")
                .formParam("client_secret", "cuv0nz1enzpp8aTLruUOLthU6NEyU0vs")
                .when()
                .post("http://localhost:8888/realms/travelerent/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }
}

