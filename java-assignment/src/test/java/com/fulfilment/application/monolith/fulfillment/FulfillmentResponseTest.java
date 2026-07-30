package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import org.junit.jupiter.api.Test;

class FulfillmentResponseTest {

  @Test
  void mapsAssignmentToResponse() {
    Product product = new Product();
    product.id = 11L;
    Store store = new Store();
    store.id = 22L;
    DbWarehouse warehouse = new DbWarehouse();
    warehouse.businessUnitCode = "MWH.100";

    FulfillmentAssignment assignment = new FulfillmentAssignment();
    assignment.id = 33L;
    assignment.product = product;
    assignment.store = store;
    assignment.warehouse = warehouse;

    FulfillmentResponse response = FulfillmentResponse.from(assignment);

    assertEquals(33L, response.id);
    assertEquals(11L, response.productId);
    assertEquals(22L, response.storeId);
    assertEquals("MWH.100", response.warehouseBusinessUnitCode);
  }
}
