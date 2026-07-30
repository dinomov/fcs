package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ReplaceWarehouseOperation {
  void replace(@NotNull @Valid Warehouse warehouse);
}
