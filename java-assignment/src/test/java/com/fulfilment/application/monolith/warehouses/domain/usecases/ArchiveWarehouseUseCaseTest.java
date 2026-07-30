package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  @Test
  void archivesWarehouseBySettingArchiveTimeAndUpdatingStore() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.100";
    store.warehouses.add(warehouse);

    new ArchiveWarehouseUseCase(store).archive(warehouse);

    assertNotNull(warehouse.archivedAt);
  }

  @Test
  void rejectsNullWarehouse() {
    assertEquals(
        "Warehouse does not exist",
        assertThrows(
                IllegalArgumentException.class,
                () ->
                    new ArchiveWarehouseUseCase(new CreateWarehouseUseCaseTest.InMemoryWarehouseStore())
                        .archive(null))
            .getMessage());
  }

  @Test
  void keepsExistingArchiveTime() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.100";
    warehouse.archivedAt = java.time.LocalDateTime.of(2024, 1, 1, 1, 1);

    new ArchiveWarehouseUseCase(store).archive(warehouse);

    assertEquals(java.time.LocalDateTime.of(2024, 1, 1, 1, 1), warehouse.archivedAt);
  }
}
