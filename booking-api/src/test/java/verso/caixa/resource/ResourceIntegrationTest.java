package verso.caixa.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.helpers.BookingTestHelper;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.repository.BookingDAO;
import verso.caixa.service.BookingService;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
public class ResourceIntegrationTest {

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingDAO bookingDAO = Mockito.mock(BookingDAO.class);
        VehicleAPIClient vehicleAPIClient = Mockito.mock(VehicleAPIClient.class);
        BookingMapper bookingMapper = Mockito.mock(BookingMapper.class);

        Mockito.when(vehicleAPIClient.findVehicleById(Mockito.any(UUID.class))).thenReturn(new VehicleAPIClient.Vehicle(
                "AVAILABLE"
        ));


        BookingService bookingService = new BookingService(bookingMapper, bookingDAO, vehicleAPIClient);

        BookingModel fakeBooking = BookingTestHelper.buildValidBooking();

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(UUID.randomUUID(),
                "Sara Campos",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3));

        Mockito.when(bookingMapper.toEntity(dto)).thenReturn(fakeBooking);

        Assertions.assertDoesNotThrow(() -> {
            bookingService.createBooking(dto);
        });
    }


        /*
        var requestBody = """
            {
              "vehicleId": "%s",
              "customerName": "Bruno Farias",
              "startDate": "%s",
              "endDate": "%s"
            }
            """.formatted(
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/bookings")
                .then()
                .statusCode(201)
                .body("customerName", equalTo("Bruno Farias"))
                .body("startDate", equalTo(LocalDate.now().plusDays(1).toString()))
                .body("endDate", equalTo(LocalDate.now().plusDays(5).toString()))
                .body("status", equalTo("CREATED"));
    }*/

    @Test
    void shouldReturnBadRequestForBookingWithInvalidStartDate() throws JsonProcessingException {

        CreateBookingRequestDTO bookingDto = new CreateBookingRequestDTO(UUID.randomUUID(),
                "Sara Campos",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(3));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String bookingJson = mapper.writeValueAsString(bookingDto);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/api/v1/bookings")
                .then()
                .statusCode(400)
                .body("violations[0].message", equalTo("A data de início não pode ser anterior a hoje."));
    }

    /*@Test
    void shouldCancelBookingSuccessfully() throws JsonProcessingException {
        CreateBookingRequestDTO bookingDto = new CreateBookingRequestDTO(
                UUID.randomUUID(),
                "Marcos Vinicius",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String bookingJson = mapper.writeValueAsString(bookingDto);

        //Executa POST e extrai o Location
        String location = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/api/v1/bookings")
                .then()
                .log().all()
                .statusCode(201)
                .header("Location", startsWith("http://localhost:8081/api/v1/bookings/"))
                .extract()
                .header("Location");

        // Obtém o ID extraindo a parte final da URL
        String bookingId = location.substring(location.lastIndexOf("/") + 1);

        // Monta o JSON de cancelamento
        var cancelJson = """
            {
              "status": "CANCELED"
            }
            """;

        // Chama o PATCH para cancelar e verifica 204 No Content
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(cancelJson)
                .when()
                .patch("/api/v1/bookings/{id}", bookingId)
                .then()
                .statusCode(204);

        //  confirmar que o status realmente mudou.
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/bookings/{id}", bookingId)
                .then()
                .statusCode(200)
                .body("entity.status", equalTo("CANCELED"))
                .body("entity.bookingId", equalTo(bookingId));
    }*/
}
