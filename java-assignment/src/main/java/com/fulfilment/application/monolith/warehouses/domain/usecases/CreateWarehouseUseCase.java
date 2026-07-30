package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Objects;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    validateWarehouse(warehouse);

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException("Business unit code is already in use");
    }

    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location does not exist");
    }

    long warehousesAtLocation =
        warehouseStore.getAll().stream()
            .filter(existing -> warehouse.location.equals(existing.location))
            .count();
    if (warehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached at location");
    }

    if (warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Warehouse capacity exceeds location capacity");
    }

    int totalCapacityAtLocation =
        warehouseStore.getAll().stream()
            .filter(existing -> warehouse.location.equals(existing.location))
            .map(existing -> existing.capacity)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
    if (totalCapacityAtLocation + warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Location does not have enough capacity");
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Warehouse stock exceeds warehouse capacity");
    }

    if (warehouse.createdAt == null) {
      warehouse.createdAt = LocalDateTime.now();
    }

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }

  private void validateWarehouse(Warehouse warehouse) {
    if (warehouse == null
        || isBlank(warehouse.businessUnitCode)
        || isBlank(warehouse.location)
        || warehouse.capacity == null
        || warehouse.stock == null
        || warehouse.capacity < 0
        || warehouse.stock < 0) {
      throw new IllegalArgumentException("Warehouse data is invalid");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
