package com.fulfilment.application.monolith.stores;

public final class StoreEvent {

  private final Type type;
  private final Store store;

  public StoreEvent(Type type, Store store) {
    this.type = type;
    this.store = store;
  }

  public Type type() {
    return type;
  }

  public Store store() {
    return store;
  }

  public enum Type {
    CREATED,
    UPDATED
  }
}
