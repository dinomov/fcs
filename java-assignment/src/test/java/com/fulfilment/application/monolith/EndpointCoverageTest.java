package com.fulfilment.application.monolith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EndpointCoverageTest {

  @Test
  void exercisesProductCreateReadUpdateAndDelete() {
    String name = "COVERAGE-PRODUCT";
    Response created =
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"" + name + "\",\"description\":\"test\",\"price\":12.50,\"stock\":4}")
            .post("/product")
            .then()
            .statusCode(201)
            .body("name", equalTo(name), "id", notNullValue())
            .extract()
            .response();

    String id = created.path("id").toString();
    given().get("/product/" + id).then().statusCode(200).body("name", equalTo(name));

    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"COVERAGE-PRODUCT-UPDATED\",\"stock\":9}")
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("COVERAGE-PRODUCT-UPDATED"), "stock", equalTo(9));

    given().delete("/product/" + id).then().statusCode(204);
    given().get("/product/" + id).then().statusCode(404);
  }

  @Test
  void exercisesStoreCreateReadUpdatePatchAndDelete() {
    String name = "COVERAGE-STORE";
    Response created =
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"" + name + "\",\"quantityProductsInStock\":3}")
            .post("/store")
            .then()
            .statusCode(201)
            .body("name", equalTo(name), "id", notNullValue())
            .extract()
            .response();

    String id = created.path("id").toString();
    given().get("/store").then().statusCode(200).body("name", hasItem(name));
    given().get("/store/" + id).then().statusCode(200).body("name", equalTo(name));

    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"COVERAGE-STORE-UPDATED\",\"quantityProductsInStock\":7}")
        .put("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("COVERAGE-STORE-UPDATED"), "quantityProductsInStock", equalTo(7));

    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"COVERAGE-STORE-PATCHED\",\"quantityProductsInStock\":8}")
        .patch("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("COVERAGE-STORE-PATCHED"), "quantityProductsInStock", equalTo(8));

    given().delete("/store/" + id).then().statusCode(204);
    given().get("/store/" + id).then().statusCode(404);
  }

  @Test
  void exercisesFulfillmentAssociateListAndRemove() {
    Response product =
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"COVERAGE-FULFILLMENT-PRODUCT\",\"stock\":1}")
            .post("/product")
            .then()
            .statusCode(201)
            .extract()
            .response();
    String productId = product.path("id").toString();

    String request =
        "{\"productId\":" + productId + ",\"storeId\":2,\"warehouseBusinessUnitCode\":\"MWH.012\"}";
    Response assignment =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/fulfillment")
            .then()
            .statusCode(201)
            .body("productId", equalTo(Integer.valueOf(productId)), "storeId", equalTo(2))
            .extract()
            .response();
    String assignmentId = assignment.path("id").toString();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/fulfillment")
        .then()
        .statusCode(201)
        .body("id", equalTo(Integer.valueOf(assignmentId)));
    given().get("/fulfillment").then().statusCode(200).body("id", hasItem(Integer.valueOf(assignmentId)));

    given().delete("/fulfillment/" + assignmentId).then().statusCode(204);
    given().delete("/fulfillment/" + assignmentId).then().statusCode(400);
  }

  @Test
  void exercisesWarehouseCreateGetReplaceAndArchive() {
    String code = "MWH.COVERAGE";
    String request = "{\"businessUnitCode\":\"" + code + "\",\"location\":\"AMSTERDAM-002\",\"capacity\":20,\"stock\":5}";

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .post("/warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo(code), "location", equalTo("AMSTERDAM-002"));
    given().get("/warehouse/" + code).then().statusCode(200).body("capacity", equalTo(20));

    given()
        .contentType(ContentType.JSON)
        .body("{\"location\":\"AMSTERDAM-002\",\"capacity\":25,\"stock\":5}")
        .post("/warehouse/" + code + "/replacement")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo(code), "capacity", equalTo(25));

    given().delete("/warehouse/" + code).then().statusCode(204);
    given().get("/warehouse/" + code).then().statusCode(404);
    given().get("/warehouse/archived").then().statusCode(200).body("businessUnitCode", hasItem(code));
  }

  @Test
  void exercisesProductAndStoreValidationErrors() {
    given().get("/product/999999").then().statusCode(404).body("code", equalTo(404));
    given()
        .contentType(ContentType.JSON)
        .body("{\"id\":99,\"name\":\"INVALID\"}")
        .post("/product")
        .then()
        .statusCode(422);
    given()
        .contentType(ContentType.JSON)
        .body("{\"stock\":2}")
        .put("/product/999999")
        .then()
        .statusCode(422);
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"MISSING\"}")
        .put("/product/999999")
        .then()
        .statusCode(404);
    given().delete("/product/999999").then().statusCode(404);

    given().get("/store/999999").then().statusCode(404).body("code", equalTo(404));
    given()
        .contentType(ContentType.JSON)
        .body("{\"id\":99,\"name\":\"INVALID\"}")
        .post("/store")
        .then()
        .statusCode(422);
    given()
        .contentType(ContentType.JSON)
        .body("{\"quantityProductsInStock\":2}")
        .put("/store/999999")
        .then()
        .statusCode(422);
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"MISSING\"}")
        .put("/store/999999")
        .then()
        .statusCode(404);
    given().delete("/store/999999").then().statusCode(404);
  }

  @Test
  void exercisesFulfillmentAndWarehouseValidationErrors() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .post("/fulfillment")
        .then()
        .statusCode(400);
    given()
        .contentType(ContentType.JSON)
        .body("{\"productId\":999999,\"storeId\":1,\"warehouseBusinessUnitCode\":\"MWH.001\"}")
        .post("/fulfillment")
        .then()
        .statusCode(400);
    given().delete("/fulfillment/999999").then().statusCode(400);

    given().get("/warehouse/UNKNOWN-COVERAGE").then().statusCode(404);
    given().delete("/warehouse/UNKNOWN-COVERAGE").then().statusCode(404);
    given()
        .contentType(ContentType.JSON)
        .body("{\"businessUnitCode\":\"MWH.INVALID\",\"location\":\"UNKNOWN\",\"capacity\":1,\"stock\":1}")
        .post("/warehouse")
        .then()
        .statusCode(400);
  }
}
