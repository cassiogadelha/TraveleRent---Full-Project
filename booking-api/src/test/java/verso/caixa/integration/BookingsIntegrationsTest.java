package verso.caixa.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.*;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.BookingDAO;
import verso.caixa.repository.VehicleStatusDAO;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@QuarkusTest
//@QuarkusTestResource(WireMockVehicleAPI.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingsIntegrationsTest {

    VehicleStatus vehicleStatusTest;
    UUID createdBookingId;

    @Inject
    VehicleStatusDAO vehicleStatusDAO;

    @Inject
    @Channel("vehicle-status-changed-out")
    Emitter<VehicleProducerDTO> emitter;

    @Inject
    BookingDAO bookingDAO;


    @BeforeEach
    @Transactional
    void setupVehicleStatus() {
        vehicleStatusTest = new VehicleStatus(UUID.randomUUID(), "AVAILABLE");
        vehicleStatusDAO.persist(vehicleStatusTest);
    }

    @Test
    @Order(1)
    void shouldReturnEmptyListMessageAsAdmin() {
        String token = BookingTestHelper.getAdminAccessToken();

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings")
            .then()
            .statusCode(200)
            .body("mensagem", equalTo("A lista de agendamentos está vazia."));
    }

    @Test
    @Order(2)
    void shouldReturnEmptyListMessageAsCostumer() {
        String token = BookingTestHelper.getAnaAccessToken();

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings/my")
            .then()
            .statusCode(200)
            .body("mensagem", equalTo("Você não possui nenhum agendamento ainda."));
    }

    @Test
    @Order(3)
    void shouldCreateBookingSuccessfully() {
        String token = BookingTestHelper.getAnaAccessToken();

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
            vehicleStatusTest.getId(),
            LocalDate.now(),
            LocalDate.now().plusDays(5)
        );

        Response response = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/v1/bookings");

        response
            .then()
            .statusCode(201)
            .body("customerName", equalTo("Ana Souza"))
            .body("startDate", equalTo(dto.startDate().toString()))
            .body("endDate", equalTo(dto.endDate().toString()));

        String locationHeader = response.getHeader("Location");
        createdBookingId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));
    }

    @Test
    @Order(4)
    void shouldReturnAllBookingsAsAdmin() {
        String token = BookingTestHelper.getAdminAccessToken();
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings")
            .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body("[0].customerName", equalTo("Ana Souza"));
    }

    @Test
    @Order(5)
    void shouldReturnBadRequestForBookingWithInvalidStartDate() throws JsonProcessingException {

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
            UUID.randomUUID(),
            LocalDate.now().minusDays(2),
            LocalDate.now().plusDays(3));

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + BookingTestHelper.getAdminAccessToken()) // simula token válido
            .body(dto)
            .when()
            .post("/api/v1/bookings")
            .then()
            .statusCode(400)
            .body("violations", not(empty()))
            .body("violations.message", hasItem("A data de início não pode ser anterior a hoje."));
    }

    @Test
    @Order(6)
    void shouldGetBookingById() {
        String token = BookingTestHelper.getAdminAccessToken();

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings/" + createdBookingId)
            .then()
            .log().body()
            .statusCode(200)
            .body("entity.customerName", equalTo("Ana Souza"));
    }


    @Test
    @Order(7)
    void customerShouldOnlySeeOwnBookings() {

        String carlosToken = BookingTestHelper.getCarlosAccessToken();

        given()
            .header("Authorization", "Bearer " + carlosToken)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings/my")
            .then()
            .statusCode(200)
            .body("mensagem", equalTo("Você não possui nenhum agendamento ainda."));

        String anaToken = BookingTestHelper.getAnaAccessToken();

        given()
            .header("Authorization", "Bearer " + anaToken)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings/my")
            .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body("[0].customerName", equalTo("Ana Souza"));
    }

    @Test
    @Order(8)
    void shouldReturnAllVehiclesStatus() {
        String token = BookingTestHelper.getAdminAccessToken();
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/v1/bookings/vehicles-status")
            .then()
            .statusCode(200)
            .body("$", not(empty()));
    }

    @Test
    @Order(9)
    void shouldUpdateStatusFromCreatedToActivated() {
        String employeeToken = BookingTestHelper.getEmployeeAccessToken();

        given()
            .header("Authorization", "Bearer " + employeeToken)
            .contentType(ContentType.JSON)
            .body("{\"newStatus\": \"ACTIVATED\"}")
            .when()
            .patch("/api/v1/bookings/" + createdBookingId)
            .then()
            .statusCode(204);
    }

    @Test
    @Order(10)
    void shouldUpdateStatusFromActivatedToFinished() {
        String employeeToken = BookingTestHelper.getEmployeeAccessToken();

        given()
            .header("Authorization", "Bearer " + employeeToken)
            .contentType(ContentType.JSON)
            .body("{\"newStatus\": \"FINISHED\"}")
            .when()
            .patch("/api/v1/bookings/" + createdBookingId)
            .then()
            .statusCode(204);
    }

    @Test
    @Order(11)
    void shouldUpdateStatusFromCreatedToCanceled() {
        String employeeToken = BookingTestHelper.getEmployeeAccessToken();
        String anaToken = BookingTestHelper.getAnaAccessToken();

        CreateBookingRequestDTO dto = BookingTestHelper.buildCustomBookingDTO(
            vehicleStatusTest.getId(),
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(4)
        );

        Response response = given()
            .header("Authorization", "Bearer " + anaToken)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/v1/bookings");

        Log.info(response.getHeader("Location"));
        UUID bookingId = UUID.fromString(response.getHeader("Location").split("/")[6]);

        given()
            .header("Authorization", "Bearer " + employeeToken)
            .contentType(ContentType.JSON)
            .body("{\"newStatus\": \"CANCELED\"}")
            .when()
            .patch("/api/v1/bookings/" + bookingId)
            .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + BookingTestHelper.getAdminAccessToken())
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/bookings/" + bookingId)
            .then()
            .statusCode(200)
            .body("entity.status", equalTo("CANCELED"));

    }

    @Test
    @Order(12)
    void testGetVehicleStatusReturnsList() {
        String token = BookingTestHelper.getAdminAccessToken();

        QuarkusTransaction.begin();
        vehicleStatusDAO.persist(new VehicleStatus(UUID.randomUUID(), "AVAILABLE"));
        vehicleStatusDAO.persist(new VehicleStatus(UUID.randomUUID(), "UNDER_MAINTENANCE"));
        vehicleStatusDAO.persist(new VehicleStatus(UUID.randomUUID(), "AVAILABLE"));
        QuarkusTransaction.commit();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v1/bookings/vehicles-status")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("[0].status", notNullValue())
                .body("[1].status", notNullValue())
                .body("[1].status", notNullValue());
    }

    @Test
    @Order(13)
    void testGetVehicleStatusReturnsEmptyMessageWhenNoData() {
        String token = BookingTestHelper.getAdminAccessToken();

        QuarkusTransaction.begin();
        vehicleStatusDAO.deleteAll();
        QuarkusTransaction.commit();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v1/bookings/vehicles-status")
                .then()
                .statusCode(200)
                .body("mensagem", equalTo("A lista de VehiclesStatus está vazia."));
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

}
