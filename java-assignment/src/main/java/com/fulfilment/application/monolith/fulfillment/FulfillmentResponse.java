package com.fulfilment.application.monolith.fulfillment;

public class FulfillmentResponse {

  public Long id;
  public Long productId;
  public Long storeId;
  public String warehouseBusinessUnitCode;

  public static FulfillmentResponse from(FulfillmentAssignment assignment) {
    var response = new FulfillmentResponse();
    response.id = assignment.id;
    response.productId = assignment.product.id;
    response.storeId = assignment.store.id;
    response.warehouseBusinessUnitCode = assignment.warehouse.businessUnitCode;
    return response;
  }
}
