# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I would refactor the persistence layer toward one consistent application architecture, 
but I would do it incrementally rather than rewriting everything at once.

Currently, the codebase mixes several strategies:
• Store uses Panache Active Record with static calls such as Store.findById() and Store.listAll().
• Product uses an injected ProductRepository.
• Warehouse uses a domain port (WarehouseStore) with a Panache repository adapter.

Using different database-access styles makes transaction ownership and testing unclear. 
It also ties business and REST code directly to Panache/Hibernate, making future database changes harder.

I would standardize on the repository approach:
REST resource
    → application service
        → repository interface
            → Panache/JPA adapter

One more thing, my current solution for the legacy update, 
there is one issue: Transactional outbox AFTER_SUCCESS works only while the application is running. 
If the application crashes after the database commit, 
the legacy update can be lost. 
Instead, save an outbox event in the same transaction as the store change. 
A background worker can then publish it with retries, idempotency, monitoring, 
and dead-letter handling.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
I would use OpenAPI for all public APIs and keep the generated code at the API boundary.
OpenAPI provides a clear contract, consistent documentation, validation, and easy client generation. 
The downside is additional build tooling and less flexibility when the generated code does not match the application design.
For production, I would define the contract in OpenAPI, generate interfaces and DTOs, 
and keep business logic in handwritten application services. This gives consistency for Product, Store, 
and Warehouse APIs while keeping generated code separate from the domain.

If consider pros and cons of each approachCoded directly:
Coding endpoints directly:
Pros:
  - Fast to build, no extra setup
  - Only one thing to maintain (the code)
  - For a small internal API, coding directly (code-first) is the better default
Cons:
  - Other teams can't start integrating until backend is done
  - No design review before coding starts
  - Easy to make validation/error handling inconsistent
  - API changes whenever the database model changes

OpenAPI: 
Pros:
  - Other teams can build against the spec while backend is still in progress
  - API design gets reviewed before any code is written
  - Can auto-generate documentation and client code from the spec
  - Keeps the API separate from the database structure
Cons:
  - More upfront work and process
  - Two things to keep in sync (spec and code)
  - Slower for quick prototypes or small internal tools

```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would start with unit tests for business rules and edge cases because they are fast and provide the most feedback. 
I would then add integration tests for database repositories and REST endpoints, especially warehouse replacement and store transaction events.
I would run these tests in CI on every change, 
and focus on important branches rather than chasing a coverage percentage. 
New business rules should always include tests, and integration tests should verify the real database behavior.
```