# Assignment 5: ORM Persistence For UNO

## Context

Continue from your Assignment 4 UNO project.

Assignment 5 adds persistence to the game. The goal is to store game history and query useful player statistics.

## Main Goal

Use a Java ORM or structured persistence framework to persist UNO game results.

Allowed tools include:

- MyBatis
- Hibernate or JPA
- Spring Data JPA
- jOOQ
- another Java ORM-like persistence mapper, if approved by the instructor

## Required Work

### 1. Database Schema

Create a schema for storing game history.

The schema must support:

- players
- games
- rounds
- scores
- winner
- timestamp

Use a database suitable for local development and testing.

Acceptable examples:

- H2
- SQLite
- PostgreSQL
- MySQL or MariaDB

### 2. ORM Or Persistence Mapping

Configure the selected persistence framework.

Your code should use repository, DAO, mapper, or equivalent persistence classes. Game logic should not contain raw SQL directly.

### 3. Persist Game Results

At minimum, persist:

- player names
- game start/end or completion timestamp
- rounds played
- per-player scores
- final winner

### 4. Query Features

Implement at least three query/report features:

- list recent games
- show player win count
- show highest scores

These may be exposed through CLI commands, menu options, or a documented report mode.

### 5. Persistence Tests

Add tests for the persistence layer.

Tests should use a test database or isolated local setup. They must not depend on a developer's private machine state.

### 6. Documentation

Update `README.md` or add `docs/database.md`.

Document:

- selected database
- selected ORM/persistence framework
- schema setup
- how to run persistence tests
- how to view game history or statistics

## Deliverables

Submit:

- schema or migration/setup script
- ORM/persistence configuration
- persistence classes
- query/report implementation
- persistence tests
- database documentation

## Constraints

- Do not store only plain text logs and call that persistence.
- Do not put database credentials directly in source code.
- Do not require a manually preconfigured private database without documentation.
- Do not remove existing build, logging, Docker, or test functionality from Assignment 4.

