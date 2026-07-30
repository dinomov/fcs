package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "fulfillment_assignment",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_fulfillment_product_store_warehouse",
            columnNames = {"product_id", "store_id", "warehouse_id"}))
public class FulfillmentAssignment {

  @Id @GeneratedValue public Long id;

  @ManyToOne(optional = false)
  public Product product;

  @ManyToOne(optional = false)
  public Store store;

  @ManyToOne(optional = false)
  public DbWarehouse warehouse;
}
