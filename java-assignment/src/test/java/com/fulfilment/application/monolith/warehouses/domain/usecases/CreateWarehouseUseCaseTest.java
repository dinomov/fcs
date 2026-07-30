package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  @Test
  void createsValidWarehouseAndSetsCreationTime() {
    InMemoryWarehouseStore store = new InMemoryWarehouseStore();
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(store, id -> new Location(id, 2, 100));
    Warehouse warehouse = warehouse("MWH.100", "ZWOLLE-001", 40, 10);

    useCase.create(warehouse);

    assertEquals(1, store.warehouses.size());
    assertNotNull(warehouse.createdAt);
  }

  @Test
  void rejectsUnknownLocation() {
    CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(new InMemoryWarehouseStore(), id -> null);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse("MWH.100", "UNKNOWN", 40, 10)));

    assertEquals("Location does not exist", exception.getMessage());
  }

  @Test
  void rejectsStockGreaterThanCapacity() {
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore(), id -> new Location(id, 2, 100));

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.create(warehouse("MWH.100", "ZWOLLE-001", 10, 11)));
  }

  @Test
  void rejectsDuplicateBusinessUnitCode() {
    InMemoryWarehouseStore store = new InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.100", "ZWOLLE-001", 20, 10));
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(store, id -> new Location(id, 2, 100));

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.create(warehouse("MWH.100", "ZWOLLE-001", 20, 10)));
  }

  @Test
  void rejectsMissingWarehouseData() {
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore(), id -> new Location(id, 2, 100));

    assertInvalid(useCase, null);
    assertInvalid(useCase, warehouse(" ", "ZWOLLE-001", 20, 10));
    assertInvalid(useCase, warehouse("MWH.100", " ", 20, 10));

    Warehouse missingCapacity = warehouse("MWH.100", "ZWOLLE-001", 20, 10);
    missingCapacity.capacity = null;
    assertInvalid(useCase, missingCapacity);

    Warehouse missingStock = warehouse("MWH.100", "ZWOLLE-001", 20, 10);
    missingStock.stock = null;
    assertInvalid(useCase, missingStock);
  }

  @Test
  void rejectsNegativeCapacityOrStock() {
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore(), id -> new Location(id, 2, 100));

    assertInvalid(useCase, warehouse("MWH.100", "ZWOLLE-001", -1, 0));
    assertInvalid(useCase, warehouse("MWH.100", "ZWOLLE-001", 1, -1));
  }

  @Test
  void rejectsWhenLocationWarehouseLimitIsReached() {
    InMemoryWarehouseStore store = new InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.001", "ZWOLLE-001", 20, 10));
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(store, id -> new Location(id, 1, 100));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.create(warehouse("MWH.100", "ZWOLLE-001", 20, 10)));

    assertEquals("Maximum number of warehouses reached at location", exception.getMessage());
  }

  @Test
  void rejectsWhenLocationCapacityWouldBeExceeded() {
    InMemoryWarehouseStore store = new InMemoryWarehouseStore();
    store.warehouses.add(warehouse("MWH.001", "ZWOLLE-001", 80, 10));
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(store, id -> new Location(id, 3, 100));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.create(warehouse("MWH.100", "ZWOLLE-001", 30, 10)));

    assertEquals("Location does not have enough capacity", exception.getMessage());
  }

  @Test
  void rejectsWarehouseCapacityAboveLocationMaximum() {
    CreateWarehouseUseCase useCase =
        new CreateWarehouseUseCase(new InMemoryWarehouseStore(), id -> new Location(id, 2, 100));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.create(warehouse("MWH.100", "ZWOLLE-001", 101, 10)));

    assertEquals("Warehouse capacity exceeds location capacity", exception.getMessage());
  }

  private void assertInvalid(CreateWarehouseUseCase useCase, Warehouse warehouse) {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
    assertEquals("Warehouse data is invalid", exception.getMessage());
  }

  private Warehouse warehouse(String code, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }

  static class InMemoryWarehouseStore implements WarehouseStore {
    final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses.stream().filter(warehouse -> warehouse.archivedAt == null).toList();
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {}

    @Override
    public void remove(Warehouse warehouse) {
      warehouses.remove(warehouse);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return getAll().stream()
          .filter(warehouse -> buCode.equals(warehouse.businessUnitCode))
          .findFirst()
          .orElse(null);
    }
  }
}
