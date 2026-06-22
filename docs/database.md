# Database Documentation (Assignment 5)

## Technology choices

| Concern         | Choice                                    |
|-----------------|-------------------------------------------|
| ORM / mapping   | Hibernate 6 (Jakarta Persistence / JPA)   |
| Database        | H2 (embedded — no external install needed)|
| Connection pool | Hibernate default (single connection)     |

H2 was chosen because it requires no installation, works identically on every machine, and supports both file-based storage (for the game) and in-memory mode (for tests).

---

## Schema

Hibernate auto-creates the schema on first run (`hbm2ddl.auto=update`).
The three tables map to the three JPA entity classes:

### `game_session`
| Column        | Type            | Notes                             |
|---------------|-----------------|-----------------------------------|
| id            | BIGINT (PK)     | auto-generated                    |
| started_at    | TIMESTAMP       | when the session started          |
| finished_at   | TIMESTAMP       | when the session ended            |
| winner_name   | VARCHAR         | player with highest total score   |
| total_rounds  | INTEGER         | number of rounds played           |

### `round`
| Column        | Type            | Notes                             |
|---------------|-----------------|-----------------------------------|
| id            | BIGINT (PK)     | auto-generated                    |
| session_id    | BIGINT (FK)     | references game_session.id        |
| round_number  | INTEGER         | 1-based index within the session  |
| winner_name   | VARCHAR         | player who went out this round    |
| points_scored | INTEGER         | points collected by the winner    |

### `player_score`
| Column        | Type            | Notes                             |
|---------------|-----------------|-----------------------------------|
| id            | BIGINT (PK)     | auto-generated                    |
| session_id    | BIGINT (FK)     | references game_session.id        |
| player_name   | VARCHAR         | bot or human name                 |
| total_score   | INTEGER         | accumulated score across all rounds|
| winner        | BOOLEAN         | true for the session winner       |

---

## Database file location

The production database is stored as `uno-history.mv.db` in the working directory
from which the application is launched.  The file is auto-created on first run.
It is listed in `.gitignore` and not committed to the repository.

The H2 credentials (`sa` / empty password) are H2's well-known defaults for
embedded local-only use. There are no sensitive credentials.

---

## Schema setup

No manual setup is required.  Hibernate creates the schema automatically on
first connection (`hbm2ddl.auto=update`).

---

## Running persistence tests

Persistence tests use an isolated **in-memory H2 database** (`uno-test-pu`).
The schema is created fresh before each test run and dropped afterwards.
No external database or manual setup is needed.

```bash
# runs both CharacterizationTest and PersistenceTest
./mvnw test          # Linux/macOS
.\mvnw.cmd test      # Windows PowerShell
```

---

## Viewing game history and statistics

After playing one or more games, run:

```bash
# with the fat jar
java -jar target/midterm-uno.jar --stats

# with Maven exec
./mvnw exec:java -Dexec.mainClass=Main "-Dexec.args=--stats"
.\mvnw.cmd exec:java -Dexec.mainClass=Main "-Dexec.args=--stats"

# with Docker
docker run --rm -v "$PWD/uno-history.mv.db:/app/uno-history.mv.db" midterm-uno --stats
```

The `--stats` output shows three reports:

1. **Recent Games** — last 5 sessions (start time, winner, rounds played)
2. **Player Win Counts** — how many sessions each player has won, descending
3. **Highest Session Scores** — top 5 individual session totals

---

## Persistence classes

| Class                  | Role                                                |
|------------------------|-----------------------------------------------------|
| `GameSessionEntity`    | JPA entity for a complete session                   |
| `RoundEntity`          | JPA entity for one round                            |
| `PlayerScoreEntity`    | JPA entity for per-player score                     |
| `GameRepository`       | DAO — all JPQL queries live here, no SQL in Main    |
| `PersistenceService`   | Service facade called by Main (no JPA in game logic)|

