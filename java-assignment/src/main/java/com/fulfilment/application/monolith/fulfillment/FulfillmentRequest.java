package com.fulfilment.application.monolith.fulfillment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FulfillmentRequest {

  @NotNull public Long productId;

  @NotNull public Long storeId;

  @NotBlank public String warehouseBusinessUnitCode;
}
