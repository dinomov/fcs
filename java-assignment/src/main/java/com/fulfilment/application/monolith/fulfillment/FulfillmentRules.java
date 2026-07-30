package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import java.util.List;
import java.util.Objects;

final class FulfillmentRules {

  private FulfillmentRules() {}

  static void validateNewAssociation(
      List<FulfillmentAssignment> assignments,
      Product product,
      Store store,
      DbWarehouse warehouse) {
    long productWarehouseCount =
        assignments.stream()
            .filter(assignment -> Objects.equals(assignment.product.id, product.id))
            .filter(assignment -> Objects.equals(assignment.store.id, store.id))
            .map(assignment -> assignment.warehouse.id)
            .distinct()
            .count();
    if (productWarehouseCount >= 2) {
      throw new IllegalArgumentException(
          "A product can be fulfilled by at most 2 warehouses per store");
    }

    long storeWarehouseCount =
        assignments.stream()
            .filter(assignment -> Objects.equals(assignment.store.id, store.id))
            .map(assignment -> assignment.warehouse.id)
            .distinct()
            .count();
    if (storeWarehouseCount >= 3) {
      throw new IllegalArgumentException("A store can be fulfilled by at most 3 warehouses");
    }

    long warehouseProductCount =
        assignments.stream()
            .filter(assignment -> Objects.equals(assignment.warehouse.id, warehouse.id))
            .map(assignment -> assignment.product.id)
            .distinct()
            .count();
    if (warehouseProductCount >= 5) {
      throw new IllegalArgumentException("A warehouse can store at most 5 product types");
    }
  }
}
