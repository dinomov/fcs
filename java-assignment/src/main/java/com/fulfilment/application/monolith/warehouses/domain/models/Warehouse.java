package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Warehouse {

  public Long id;

  // unique identifier
  @NotBlank
  public String businessUnitCode;

  @NotBlank
  public String location;

  @NotNull
  @Positive
  public Integer capacity;

  @NotNull
  @Positive
  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

  // Database version used to detect concurrent warehouse replacements.
  public Long version;
}
