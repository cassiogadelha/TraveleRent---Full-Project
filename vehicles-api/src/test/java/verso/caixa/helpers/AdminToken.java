package verso.caixa.helpers;

import static io.restassured.RestAssured.given;

public class AdminToken {

    public static String getAdminAccessToken() {
        return given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "vehicles-backend-service")
                .formParam("username", "admin")
                .formParam("password", "admin")
                .formParam("grant_type", "password")
                .formParam("client_secret", "0CSfjWlFuusccCAd4oSV3AiduukffS6t")
                .when()
                .post("http://localhost:8888/realms/travelerent/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

}
