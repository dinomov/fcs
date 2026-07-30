package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  @Test
  void archivesCurrentWarehouseAndCreatesReplacement() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    Warehouse current = warehouse("MWH.100", "ZWOLLE-001", 40, 10);
    store.warehouses.add(current);
    Warehouse replacement = warehouse("MWH.100", "ZWOLLE-001", 50, 10);

    new ReplaceWarehouseUseCase(store, id -> new Location(id, 2, 100)).replace(replacement);

    assertNotNull(current.archivedAt);
    assertEquals(2, store.warehouses.size());
    assertEquals(50, store.warehouses.get(1).capacity);
  }

  @Test
  void rejectsReplacementWithDifferentStock() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));
    Warehouse replacement = warehouse("MWH.100", "ZWOLLE-001", 50, 9);

    assertThrows(
        IllegalArgumentException.class,
        () -> new ReplaceWarehouseUseCase(store, id -> new Location(id, 2, 100)).replace(replacement));
  }

  @Test
  void rejectsInvalidReplacementData() {
    var useCase =
        new ReplaceWarehouseUseCase(
            new CreateWarehouseUseCaseTest.InMemoryWarehouseStore(),
            id -> new Location(id, 2, 100));

    assertThrows(IllegalArgumentException.class, () -> useCase.replace(null));

    Warehouse replacement = warehouse("MWH.100", "ZWOLLE-001", 50, 10);
    replacement.capacity = -1;
    assertThrows(IllegalArgumentException.class, () -> useCase.replace(replacement));
  }

  @Test
  void rejectsMissingCurrentWarehouse() {
    var useCase =
        new ReplaceWarehouseUseCase(
            new CreateWarehouseUseCaseTest.InMemoryWarehouseStore(),
            id -> new Location(id, 2, 100));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.replace(warehouse("MWH.100", "ZWOLLE-001", 50, 10)));

    assertEquals("Warehouse to replace does not exist", exception.getMessage());
  }

  @Test
  void rejectsUnknownReplacementLocation() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ReplaceWarehouseUseCase(store, id -> null)
                    .replace(warehouse("MWH.100", "UNKNOWN", 50, 10)));

    assertEquals("Location does not exist", exception.getMessage());
  }

  @Test
  void rejectsCapacityThatCannotAccommodateCurrentStock() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ReplaceWarehouseUseCase(store, id -> new Location(id, 2, 100))
                    .replace(warehouse("MWH.100", "ZWOLLE-001", 9, 10)));

    assertEquals("Replacement capacity cannot accommodate current stock", exception.getMessage());
  }

  @Test
  void rejectsCapacityAboveLocationMaximum() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ReplaceWarehouseUseCase(store, id -> new Location(id, 2, 50))
                    .replace(warehouse("MWH.100", "ZWOLLE-001", 51, 10)));

    assertEquals("Warehouse capacity exceeds location capacity", exception.getMessage());
  }

  @Test
  void rejectsLocationWarehouseLimitWhenMovingReplacement() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));
    store.warehouses.add(warehouse("MWH.200", "AMSTERDAM-001", 20, 5));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 100))
                    .replace(warehouse("MWH.100", "AMSTERDAM-001", 50, 10)));

    assertEquals("Maximum number of warehouses reached at location", exception.getMessage());
  }

  @Test
  void rejectsInsufficientCapacityWhenMovingReplacement() {
    var store = new CreateWarehouseUseCaseTest.InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 40, 10));
    store.warehouses.add(warehouse("MWH.200", "AMSTERDAM-001", 80, 5));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new ReplaceWarehouseUseCase(store, id -> new Location(id, 3, 100))
                    .replace(warehouse("MWH.100", "AMSTERDAM-001", 30, 10)));

    assertEquals("Location does not have enough capacity", exception.getMessage());
  }

  private Warehouse warehouse(String code, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
