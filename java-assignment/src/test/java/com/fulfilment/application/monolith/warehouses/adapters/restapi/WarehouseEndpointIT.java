package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

  @Test
  public void listsSeededWarehouses() {
    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
  }

  @Test
  public void createsGetsReplacesAndArchivesWarehouse() {
    final String code = "MWH.IT.COVERAGE";

    given()
        .contentType("application/json")
        .body(
            "{\"businessUnitCode\":\""
                + code
                + "\",\"location\":\"EINDHOVEN-001\",\"capacity\":20,\"stock\":5}")
        .when()
        .post("warehouse")
        .then()
        .statusCode(200)
        .body(containsString(code), containsString("EINDHOVEN-001"));

    given().when().get("warehouse/" + code).then().statusCode(200).body(containsString(code));

    given()
        .contentType("application/json")
        .body("{\"location\":\"EINDHOVEN-001\",\"capacity\":25,\"stock\":5}")
        .when()
        .post("warehouse/" + code + "/replacement")
        .then()
        .statusCode(200)
        .body(containsString(code));

    given().when().delete("warehouse/" + code).then().statusCode(204);
    given().when().get("warehouse/" + code).then().statusCode(404);
  }
}
