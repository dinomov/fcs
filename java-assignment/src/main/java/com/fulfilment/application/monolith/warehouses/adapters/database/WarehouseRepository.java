package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return find("archivedAt is null").list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  public List<Warehouse> getArchived() {
    return find("archivedAt is not null").list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    var entity = new DbWarehouse();
    copy(warehouse, entity);
    persist(entity);
  }

  @Override
  @Transactional
  public void update(Warehouse warehouse) {
    if (warehouse.id == null || warehouse.version == null) {
      throw new IllegalArgumentException("Warehouse does not exist");
    }

    // Merge the version read by the use case. Hibernate rejects this update
    // if another transaction has already changed the same warehouse.
    var entity = new DbWarehouse();
    entity.id = warehouse.id;
    entity.version = warehouse.version;
    copy(warehouse, entity);
    getEntityManager().merge(entity);
  }

  @Override
  @Transactional
  public void remove(Warehouse warehouse) {
    DbWarehouse entity = findEntity(warehouse);
    if (entity != null) {
      delete(entity);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
    return entity == null ? null : entity.toWarehouse();
  }

  private DbWarehouse findEntity(Warehouse warehouse) {
    return find("businessUnitCode = ?1", warehouse.businessUnitCode).firstResult();
  }

  private void copy(Warehouse source, DbWarehouse target) {
    target.businessUnitCode = source.businessUnitCode;
    target.location = source.location;
    target.capacity = source.capacity;
    target.stock = source.stock;
    target.createdAt = source.createdAt;
    target.archivedAt = source.archivedAt;
  }
}
