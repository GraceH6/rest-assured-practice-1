package apitests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class GetPostDeleteMethodTests {
    static final String BASE_URL = "https://reqres.in";

    @Test
    void usersEmailTest() {
        given()
                .when()
                .log().uri()
                .log().body()
                .get(BASE_URL + "/api/users?page=2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.email", hasItems(endsWith("@reqres.in")));
    }

    @Test
    void getSingleUserTest() {
        given()
                .when()
                .get(BASE_URL + "/api/users/2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.last_name", is("Weaver"),
                        "data.email", endsWith("@reqres.in"))
                .body(
                        matchesJsonSchemaInClasspath("schemes/single-user-json-schema.json")
                );
    }

    @Test
    void userNotFoundTest() {
        given()
                .when()
                .get(BASE_URL + "/api/users/23")
                .then()
                .log().status()
                .statusCode(404);
    }

    @Test
    void failedRegisterTest() {
        String data = "{\"email\": \"sydney@fife\"}";
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(data)
                .post(BASE_URL + "/api/register")
                .then()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void getDeletedUserTest() {
        given()
                .when()
                .delete(BASE_URL + "/api/users/2")
                .then()
                .log().status()
                .statusCode(204);
    }
}
