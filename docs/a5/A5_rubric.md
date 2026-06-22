# Assignment 5 Rubric

## Point Value

Assignment 5 is worth:

- `8` rubric points

Course-level bonuses are defined separately in `A4_A5_Final_Project_evaluation_process.md`.

## Rubric

### 1. ORM/Framework Configuration And Schema: 2 points

- `2`: ORM or structured persistence mapper is configured correctly; schema supports all required entities and relationships
- `1.5`: configuration works, but schema or setup has minor omissions
- `1`: partial setup exists but is fragile or incomplete
- `0.5`: minimal evidence of persistence setup
- `0`: no meaningful ORM or structured persistence setup

Accepted approaches include MyBatis, Hibernate/JPA, Spring Data JPA, jOOQ, or another approved ORM-like mapper.

### 2. Persist Core Game Data: 2 points

Required data:

- players
- games
- rounds
- scores
- winner
- timestamp

Scoring:

- `2`: all required data is persisted through the game flow
- `1.5`: most required data is persisted, with minor omissions
- `1`: partial persistence exists but important data is missing
- `0.5`: only trivial or disconnected data is stored
- `0`: no meaningful game persistence

### 3. Query And Report Features: 1.5 points

Required queries:

- recent games
- player win count
- highest scores

Scoring:

- `1.5`: all three query/report features work through a documented path
- `1`: two features work clearly
- `0.5`: one feature works clearly, or query code exists but is incomplete, disconnected, or not documented
- `0`: no meaningful query/report feature

### 4. Persistence Tests: 1.5 points

- `1.5`: repository/DAO/mapper tests run against an isolated test setup
- `1`: tests exist but setup is incomplete, fragile, or depends on manual state
- `0.5`: tests are superficial or mostly disconnected from real persistence behavior
- `0`: no meaningful persistence tests

### 5. Database Documentation: 1 point

- `1`: documentation clearly explains database setup, schema/init steps, tests, and usage
- `0.5`: documentation exists but misses important setup or usage details
- `0`: no useful database documentation

## Grade Calculation

```text
ORM/Framework Configuration And Schema /2
+ Persist Core Game Data /2
+ Query And Report Features /1.5
+ Persistence Tests /1.5
+ Database Documentation /1
= Assignment 5 grade /8
```

## Grading Summary Format

```text
ORM/Framework Configuration And Schema: __/2
Persist Core Game Data: __/2
Query And Report Features: __/1.5
Persistence Tests: __/1.5
Database Documentation: __/1
Assignment 5 grade: __/8
```
