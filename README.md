# Midterm UNO CLI (A4: Maven + Logging + Docker)

This is a standalone CLI UNO-like game.

The code is written as plausible feature-grown Java: almost everything lives in one procedural `Main` class. It works, but it has mixed responsibilities, duplicated rule logic, primitive-heavy card handling, global state, and condition-heavy gameplay code. The goal is to refactor it safely, not rewrite it.

## Compile

```bash
scripts/compile.sh
```

## Run Bot Games

```bash
scripts/run.sh --bots 3 --games 5 --quiet
```

## Run Interactive Game

```bash
scripts/run.sh --human --bots 2 --games 1
```

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
```

## Characterization Checks

```bash
scripts/test.sh
```

## Submission

Submit your work through GitHub:

1. Fork this repository to your GitHub account.
2. Clone your fork locally.
3. Complete the midterm work in your fork.
4. Commit your changes with clear commit messages.
5. Push your branch to GitHub.
6. Open a pull request from your fork back to the original repository.

Your pull request must include:

* refactored source code
* characterization tests
* `docs/refactoring-report.md`
* `docs/extension-readiness.md`

Do not submit a zip file instead of a pull request unless the instructor explicitly asks for it.

## Rules

See `docs/rules.html` for the implemented game rules.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path

---

## Assignment 4 — Build, Test, Run, Docker

This project uses **Maven** (wrapper included — Maven does not need to be pre-installed).

### Local build

```bash
./mvnw compile          # Linux / macOS / Git Bash
.\mvnw.cmd compile      # Windows PowerShell
```

### Local test

```bash
./mvnw test
.\mvnw.cmd test
```

Runs `CharacterizationTest` (the hand-written test harness) via `exec-maven-plugin` bound to the `test` phase — no manual classpath setup needed.

### Local run

```bash
./mvnw exec:java -Dexec.mainClass=Main "-Dexec.args=--bots 3 --games 1"
.\mvnw.cmd exec:java -Dexec.mainClass=Main "-Dexec.args=--bots 3 --games 1"
```

Or after packaging:

```bash
java -jar target/midterm-uno.jar --bots 3 --games 1
```

### Package (build runnable fat jar)

```bash
./mvnw package
.\mvnw.cmd package
# produces: target/midterm-uno.jar
```

### Docker build

```bash
docker build -t midterm-uno .
```

### Docker run

```bash
docker run --rm -it midterm-uno --bots 3 --games 1
```

Override the default arguments freely:

```bash
docker run --rm -it midterm-uno --bots 2 --games 5 --quiet
```

### Logging

Game events (start, player turns, cards played/drawn, invalid input, round end, game end) are logged via `java.util.logging` to **stderr**, keeping player-facing **stdout** output clean and readable for human players.

---

## Assignment 5 — Persistence (ORM + H2)

Game results are persisted automatically after every session using **Hibernate 6 / JPA** and an embedded **H2** database (`uno-history.mv.db` in the working directory).

No installation or setup is required — H2 is embedded and the schema is created automatically on first run.

See [`docs/database.md`](docs/database.md) for full schema and usage details.

### View game statistics

```bash
# after packaging:
java -jar target/midterm-uno.jar --stats

# via Maven exec:
./mvnw exec:java -Dexec.mainClass=Main "-Dexec.args=--stats"
.\mvnw.cmd exec:java -Dexec.mainClass=Main "-Dexec.args=--stats"
```

Displays three reports:
- **Recent Games** — last 5 sessions (time, winner, rounds played)
- **Player Win Counts** — wins per player, descending
- **Highest Session Scores** — top 5 individual session totals

### Run persistence tests

```bash
./mvnw test          # Linux/macOS  (runs CharacterizationTest + PersistenceTest)
.\mvnw.cmd test      # Windows PowerShell
```

Persistence tests use an isolated **in-memory H2 database** — no manual setup needed.

---

## Final Project — Fuller UNO Rules

This builds on the midterm/A4/A5 work with the remaining UNO rule features:
**UNO call + missed-call penalty** and **multi-round play to a target score**.

See [`docs/rules-supported.md`](docs/rules-supported.md) for the full rule-by-rule
breakdown (what's implemented vs. simplified) and [`docs/final-report.md`](docs/final-report.md)
for the architecture/testing summary.

### New CLI flags

```
--target N   play rounds continuously until a player's cumulative score
             reaches N, then print final scores and the overall winner
             (overrides --games when provided)
```

Example — play until someone reaches 150 points:

```bash
java -jar target/midterm-uno.jar --bots 3 --target 150 --quiet --seed 42
.\mvnw.cmd exec:java -Dexec.mainClass=Main "-Dexec.args=--bots 3 --target 150 --quiet"
```

### UNO call

When any player (human or bot) is left with exactly one card, they're prompted
to (or, for bots, automatically) call UNO. A human is asked `Call UNO? y/n:`.
If a human forgets, a 2-card penalty is applied automatically at the start of
their next turn.

### Running the full test suite

```bash
./mvnw test
.\mvnw.cmd test
```

Runs 99 characterization checks (rules, effects, UNO penalty, target-score
logic) plus 12 persistence checks — 111 total, all passing.


