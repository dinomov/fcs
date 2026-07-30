package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StoreEventTest {

  @Test
  void storesTypeAndStore() {
    Store store = new Store("Amsterdam");

    StoreEvent event = new StoreEvent(StoreEvent.Type.CREATED, store);

    assertEquals(StoreEvent.Type.CREATED, event.type());
    assertSame(store, event.store());
  }
}
