package com.fulfilment.application.monolith.fulfillment;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FulfillmentRepository implements PanacheRepository<FulfillmentAssignment> {

  public FulfillmentAssignment findExisting(Long productId, Long storeId, Long warehouseId) {
    return find(
            "product.id = ?1 and store.id = ?2 and warehouse.id = ?3",
            productId,
            storeId,
            warehouseId)
        .firstResult();
  }
}
