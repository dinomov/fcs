package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null
        || newWarehouse.businessUnitCode == null
        || newWarehouse.location == null
        || newWarehouse.capacity == null
        || newWarehouse.stock == null
        || newWarehouse.capacity < 0
        || newWarehouse.stock < 0) {
      throw new IllegalArgumentException("Warehouse data is invalid");
    }

    Warehouse current = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (current == null) {
      throw new IllegalArgumentException("Warehouse to replace does not exist");
    }

    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location does not exist");
    }
    if (!Integer.valueOf(current.stock).equals(newWarehouse.stock)) {
      throw new IllegalArgumentException("Replacement stock must match current stock");
    }
    if (newWarehouse.capacity < current.stock) {
      throw new IllegalArgumentException("Replacement capacity cannot accommodate current stock");
    }
    if (newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Warehouse capacity exceeds location capacity");
    }

    long warehousesAtLocation =
        warehouseStore.getAll().stream()
            .filter(existing -> newWarehouse.location.equals(existing.location))
            .filter(existing -> !newWarehouse.businessUnitCode.equals(existing.businessUnitCode))
            .count();
    if (!newWarehouse.location.equals(current.location)
        && warehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached at location");
    }

    int otherCapacity =
        warehouseStore.getAll().stream()
            .filter(existing -> newWarehouse.location.equals(existing.location))
            .filter(existing -> !newWarehouse.businessUnitCode.equals(existing.businessUnitCode))
            .filter(existing -> existing.capacity != null)
            .mapToInt(existing -> existing.capacity)
            .sum();
    if (otherCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Location does not have enough capacity");
    }

    current.archivedAt = LocalDateTime.now();
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;

    warehouseStore.update(current);
    warehouseStore.create(newWarehouse);
  }
}
