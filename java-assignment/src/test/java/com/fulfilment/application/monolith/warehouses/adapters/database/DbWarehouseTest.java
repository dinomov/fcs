package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DbWarehouseTest {

  @Test
  void convertsDatabaseEntityToDomainWarehouse() {
    DbWarehouse entity = new DbWarehouse();
    entity.businessUnitCode = "MWH.100";
    entity.location = "ZWOLLE-001";
    entity.capacity = 50;
    entity.stock = 10;
    entity.createdAt = LocalDateTime.of(2024, 1, 2, 3, 4);
    entity.archivedAt = LocalDateTime.of(2024, 2, 3, 4, 5);
    entity.version = 7L;

    var warehouse = entity.toWarehouse();

    assertEquals(entity.businessUnitCode, warehouse.businessUnitCode);
    assertEquals(entity.location, warehouse.location);
    assertEquals(entity.capacity, warehouse.capacity);
    assertEquals(entity.stock, warehouse.stock);
    assertEquals(entity.createdAt, warehouse.createdAt);
    assertEquals(entity.archivedAt, warehouse.archivedAt);
    assertEquals(entity.version, warehouse.version);
  }
}
