package org.practice;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

//@QuarkusTest
public class HomeResourceTest {

//    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/home")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}