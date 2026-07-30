package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.junit.jupiter.api.Test;

class StoreEventObserverTest {

  @Test
  void routesCreatedEventToCreateGatewayOperation() throws Exception {
    RecordingGateway gateway = new RecordingGateway();
    StoreEventObserver observer = observerWith(gateway);
    Store store = new Store("Amsterdam");

    observer.onStoreEvent(new StoreEvent(StoreEvent.Type.CREATED, store));

    assertEquals("create", gateway.operation);
    assertSame(store, gateway.store);
  }

  @Test
  void routesUpdatedEventToUpdateGatewayOperation() throws Exception {
    RecordingGateway gateway = new RecordingGateway();
    StoreEventObserver observer = observerWith(gateway);
    Store store = new Store("Rotterdam");

    observer.onStoreEvent(new StoreEvent(StoreEvent.Type.UPDATED, store));

    assertEquals("update", gateway.operation);
    assertSame(store, gateway.store);
  }

  @Test
  void observesEventsOnlyAfterSuccessfulTransaction() throws Exception {
    Method method =
        StoreEventObserver.class.getDeclaredMethod("onStoreEvent", StoreEvent.class);

    Observes observes = null;
    for (java.lang.annotation.Annotation annotation : method.getParameterAnnotations()[0]) {
      if (annotation instanceof Observes) {
        observes = (Observes) annotation;
        break;
      }
    }

    assertNotNull(observes);
    assertEquals(TransactionPhase.AFTER_SUCCESS, observes.during());
  }

  private StoreEventObserver observerWith(RecordingGateway gateway) throws Exception {
    StoreEventObserver observer = new StoreEventObserver();
    Field field = StoreEventObserver.class.getDeclaredField("legacyStoreManagerGateway");
    field.setAccessible(true);
    field.set(observer, gateway);
    return observer;
  }

  private static class RecordingGateway extends LegacyStoreManagerGateway {
    private String operation;
    private Store store;

    @Override
    public void createStoreOnLegacySystem(Store store) {
      operation = "create";
      this.store = store;
    }

    @Override
    public void updateStoreOnLegacySystem(Store store) {
      operation = "update";
      this.store = store;
    }
  }
}
