package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfillmentService {

  @Inject ProductRepository productRepository;
  @Inject WarehouseRepository warehouseRepository;
  @Inject FulfillmentRepository fulfillmentRepository;

  @Transactional
  public FulfillmentAssignment associate(FulfillmentRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Fulfillment request is required");
    }

    Product product = productRepository.findById(request.productId);
    if (product == null) {
      throw new IllegalArgumentException("Product with id of " + request.productId + " does not exist");
    }

    Store store = Store.findById(request.storeId);
    if (store == null) {
      throw new IllegalArgumentException("Store with id of " + request.storeId + " does not exist");
    }

    DbWarehouse warehouse =
        warehouseRepository
            .find(
                "businessUnitCode = ?1 and archivedAt is null",
                request.warehouseBusinessUnitCode)
            .firstResult();
    if (warehouse == null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code of "
              + request.warehouseBusinessUnitCode
              + " does not exist");
    }

    FulfillmentAssignment existing =
        fulfillmentRepository.findExisting(product.id, store.id, warehouse.id);
    if (existing != null) {
      return existing;
    }

    FulfillmentRules.validateNewAssociation(
        fulfillmentRepository.listAll(), product, store, warehouse);

    var assignment = new FulfillmentAssignment();
    assignment.product = product;
    assignment.store = store;
    assignment.warehouse = warehouse;
    fulfillmentRepository.persist(assignment);
    return assignment;
  }

  @Transactional
  public java.util.List<FulfillmentAssignment> list() {
    return fulfillmentRepository.listAll();
  }

  @Transactional
  public void remove(Long id) {
    FulfillmentAssignment assignment = fulfillmentRepository.findById(id);
    if (assignment == null) {
      throw new IllegalArgumentException("Fulfillment assignment with id of " + id + " does not exist");
    }
    fulfillmentRepository.delete(assignment);
  }
}
