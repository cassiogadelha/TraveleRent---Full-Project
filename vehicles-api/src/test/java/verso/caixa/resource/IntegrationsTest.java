package verso.caixa.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import verso.caixa.helpers.AdminToken;
import verso.caixa.helpers.CreateMaintenanceDTOTest;
import verso.caixa.helpers.CreateVehicleDTOTest;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationsTest {

    String token;

    @BeforeEach
    void getAdminToken() {
        this.token = AdminToken.getAdminAccessToken();
    }

    @Test
    void shouldCreateVehicleAndAddMaintenance() throws JsonProcessingException {

        CreateVehicleDTOTest dto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(dto);

        Response createResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        createResponse.then().statusCode(201);

        String locationHeader = createResponse.getHeader("Location");
        String vehicleId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        CreateMaintenanceDTOTest maintenanceDto = new CreateMaintenanceDTOTest("Vibração excessiva no volante");
        String maintenanceJson = new ObjectMapper().writeValueAsString(maintenanceDto);

        Response maintenanceResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(maintenanceJson)
            .post("/api/v1/vehicles/" + vehicleId + "/maintenances");

        maintenanceResponse.then()
            .statusCode(201)
            .header("Location", containsString("/api/v1/vehicles/" + vehicleId + "/maintenances"));
    }

    @Test
    void shouldReturn404WhenVehicleNotFound() throws JsonProcessingException {
        UUID nonexistentId = UUID.randomUUID();

        CreateMaintenanceDTOTest dto = new CreateMaintenanceDTOTest("Vibração excessiva no volante");
        String maintenanceJson = new ObjectMapper().writeValueAsString(dto);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(maintenanceJson)
            .post("/api/v1/vehicles/" + nonexistentId + "/maintenances")
            .then()
            .statusCode(404);
    }

    @Test
    void shouldGetMaintenanceById() throws JsonProcessingException {
        CreateVehicleDTOTest vehicleDto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(vehicleDto);

        Response createVehicleResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        createVehicleResponse.then().statusCode(201);

        String vehicleLocationHeader = createVehicleResponse.getHeader("Location");
        String vehicleId = vehicleLocationHeader.substring(vehicleLocationHeader.lastIndexOf("/") + 1);

        CreateMaintenanceDTOTest maintenanceDto = new CreateMaintenanceDTOTest("Vibração excessiva no volante");
        String maintenanceJson = new ObjectMapper().writeValueAsString(maintenanceDto);

        Response createMaintenanceResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(maintenanceJson)
            .post("/api/v1/vehicles/" + vehicleId + "/maintenances");
        createMaintenanceResponse.then().statusCode(201);

        String maintenanceLocationHeader = createMaintenanceResponse.getHeader("Location");
        String maintenanceId = maintenanceLocationHeader.substring(maintenanceLocationHeader.lastIndexOf("/") + 1);

        given()
            .header("Authorization", "Bearer " + token)
            .get("/api/v1/vehicles/" + vehicleId + "/maintenances/" + maintenanceId)
            .then()
            .statusCode(200)
            .body("problemDescription", equalTo("Vibração excessiva no volante"))
            .body("createdAt", notNullValue());
    }

    @Test
    void shouldReturn404OnInvalidMaintenanceId() {
        UUID randomVehicleId = UUID.randomUUID();
        UUID randomMaintenanceId = UUID.randomUUID();

        given()
            .header("Authorization", "Bearer " + token)
            .get("/api/v1/vehicles/" + randomVehicleId + "/maintenances/" + randomMaintenanceId)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldCreateVehicleWithValidID() throws JsonProcessingException {

        CreateVehicleDTOTest dto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(dto);

        Response createResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        createResponse.then().statusCode(201);

        String locationHeader = createResponse.getHeader("Location");
        String vehicleId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        Assertions.assertNotNull(vehicleId);
    }

    @Test
    void shouldReturn201WhenSendAValidVehicle() throws JsonProcessingException {

        CreateVehicleDTOTest dto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(dto);

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("api/v1/vehicles")
            .then()
            .statusCode(201);
    }

    @Test
    void shouldGetVehicleByID() throws JsonProcessingException {

        CreateVehicleDTOTest dto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(dto);

        Response createResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        createResponse.then().statusCode(201);

        String locationHeader = createResponse.getHeader("Location");
        String vehicleId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        given()
            .header("Authorization", "Bearer " + token)
            .get("/api/v1/vehicles/" + vehicleId)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldGetAll() throws JsonProcessingException {
        CreateVehicleDTOTest dto = new CreateVehicleDTOTest("Fiat", "Uno", 2020, "1.0 Fire");
        String vehicleJson = new ObjectMapper().writeValueAsString(dto);

        Response createResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        dto = new CreateVehicleDTOTest("Toyota", "Corolla", 2020, "2.0  XEI");
        vehicleJson = new ObjectMapper().writeValueAsString(dto);

        createResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(vehicleJson)
            .post("/api/v1/vehicles");

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .get("api/v1/vehicles")
            .then()
            .statusCode(200);
    }

    @Test
    @Order(1)
    void shouldReturnEmptyMessageWhenNoVehicles() {
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .accept(ContentType.JSON)
            .when()
            .get("/api/v1/vehicles")
            .then()
            .statusCode(200)
            .body("mensagem", equalTo("A lista de veículos está vazia."));
    }

    @Test
    void shouldReturnBadRequestWhenBrandIsBlank() {
        var json = """
        {
          "model": "Ka",
          "brand": "",
          "year": 2020,
          "engine": "1.0"
        }
        """;

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post("/api/v1/vehicles")
            .then()
            .statusCode(400)
            .body("violations.message", hasItem("brand não pode ser vazio"));
    }

    @Test
    void shouldReturnBadRequestWhenModelIsBlank() {
        var json = """
        {
          "model": "",
          "brand": "Ford",
          "year": 2020,
          "engine": "1.0"
        }
        """;

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post("/api/v1/vehicles")
            .then()
            .statusCode(400)
            .body("violations.message", hasItem("model não pode ser vazio"));
    }

    @Test
    void shouldReturnBadRequestWhenEngineIsBlank() {
        var json = """
        {
          "model": "Ka",
          "brand": "Ford",
          "year": 2020,
          "engine": ""
        }
        """;

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post("/api/v1/vehicles")
            .then()
            .statusCode(400)
            .body("violations.message", hasItem("engine não pode ser vazio"));
    }

    @Test
    void shouldReturnBadRequestWhenYearIsMissing() {
        var json = """
        {
          "model": "Ka",
          "brand": "Ford",
          "engine": "1.0"
        }
        """;

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post("/api/v1/vehicles")
            .then()
            .statusCode(400)
            .body("violations.message", hasItem("year não pode ser nulo"));
    }

    @Test
    void shouldReturnBadRequestWhenAllFieldsAreInvalid() {
        var json = """
        {
          "model": "",
          "brand": "",
          "engine": ""
        }
        """;

        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post("/api/v1/vehicles")
            .then()
            .log().body()
            .statusCode(400)
            .body("violations.message", hasItems(
                    "model não pode ser vazio",
                    "brand não pode ser vazio",
                    "engine não pode ser vazio",
                    "year não pode ser nulo"
            ));
    }

    @Test
    @Order(2)
    void shouldReturnPaginatedVehicleListSuccessfully() {

        for (int i = 1; i <= 15; i++) {
            var json = """
        {
          "model": "Model %d",
          "brand": "Brand %d",
          "year": %d,
          "engine": "1.0"
        }
        """.formatted(i, i, 2020 + i % 5);

            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/vehicles")
                .then()
                .statusCode(201);
        }

        RestAssured.given()
            .accept(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .when()
            .get("/api/v1/vehicles")
            .then()
            .log().body()
            .statusCode(200)
            .body("size()", is(5))
            .body("[0].model", startsWith("Model"))
            .body("[0].brand", startsWith("Brand"))
            .body("[0].year", greaterThanOrEqualTo(2020))
            .body("[0].engine", equalTo("1.0"));
    }

}
