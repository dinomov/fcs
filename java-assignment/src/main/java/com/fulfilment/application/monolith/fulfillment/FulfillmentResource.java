package com.fulfilment.application.monolith.fulfillment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

  @Inject FulfillmentService fulfillmentService;

  @POST
  public Response associate(@NotNull @Valid FulfillmentRequest request) {
    try {
      var assignment = fulfillmentService.associate(request);
      return Response.status(Response.Status.CREATED)
          .entity(FulfillmentResponse.from(assignment))
          .build();
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException(exception.getMessage(), exception);
    }
  }

  @GET
  public List<FulfillmentResponse> list() {
    return fulfillmentService.list().stream().map(FulfillmentResponse::from).toList();
  }

  @DELETE
  @Path("{id}")
  public Response remove(@PathParam("id") Long id) {
    try {
      fulfillmentService.remove(id);
      return Response.noContent().build();
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException(exception.getMessage(), exception);
    }
  }
}
