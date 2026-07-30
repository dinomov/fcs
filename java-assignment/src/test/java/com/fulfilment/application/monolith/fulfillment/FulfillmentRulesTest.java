package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FulfillmentRulesTest {

  @Test
  void allowsAssociationWithinAllLimits() {
    Product product = product(1L);
    Store store = store(1L);
    DbWarehouse warehouse = warehouse(1L);

    assertDoesNotThrow(
        () -> FulfillmentRules.validateNewAssociation(List.of(), product, store, warehouse));
  }

  @Test
  void limitsProductToTwoWarehousesPerStore() {
    Product product = product(1L);
    Store store = store(1L);
    List<FulfillmentAssignment> assignments = new ArrayList<>();
    assignments.add(assignment(product, store, warehouse(1L)));
    assignments.add(assignment(product, store, warehouse(2L)));

    assertThrows(
        IllegalArgumentException.class,
        () -> FulfillmentRules.validateNewAssociation(assignments, product, store, warehouse(3L)));
  }

  @Test
  void limitsStoreToThreeWarehouses() {
    Product product = product(1L);
    Store store = store(1L);
    List<FulfillmentAssignment> assignments = new ArrayList<>();
    assignments.add(assignment(product, store, warehouse(1L)));
    assignments.add(assignment(product(2L), store, warehouse(2L)));
    assignments.add(assignment(product(3L), store, warehouse(3L)));

    assertThrows(
        IllegalArgumentException.class,
        () -> FulfillmentRules.validateNewAssociation(assignments, product, store, warehouse(4L)));
  }

  @Test
  void limitsWarehouseToFiveProducts() {
    Store store = store(1L);
    DbWarehouse warehouse = warehouse(1L);
    List<FulfillmentAssignment> assignments = new ArrayList<>();
    for (long productId = 1; productId <= 5; productId++) {
      assignments.add(assignment(product(productId), store, warehouse));
    }

    assertThrows(
        IllegalArgumentException.class,
        () -> FulfillmentRules.validateNewAssociation(assignments, product(6L), store, warehouse));
  }

  private FulfillmentAssignment assignment(Product product, Store store, DbWarehouse warehouse) {
    var assignment = new FulfillmentAssignment();
    assignment.product = product;
    assignment.store = store;
    assignment.warehouse = warehouse;
    return assignment;
  }

  private Product product(Long id) {
    var product = new Product();
    product.id = id;
    return product;
  }

  private Store store(Long id) {
    var store = new Store();
    store.id = id;
    return store;
  }

  private DbWarehouse warehouse(Long id) {
    var warehouse = new DbWarehouse();
    warehouse.id = id;
    return warehouse;
  }
}
