# Assignment 4 Rubric

## Point Value

Assignment 4 is worth:

- `8` rubric points

Course-level bonuses are defined separately in `A4_A5_Final_Project_evaluation_process.md`.

## Rubric

### 1. Maven Or Gradle Project Setup: 1.5 points

- `1.5`: correct Maven or Gradle setup; source/test layout is standard or explicitly configured; dependencies, tests, main class, and packaging are handled by the build tool
- `1`: build tool exists but layout, dependencies, packaging, or main-class configuration has notable problems
- `0`: no meaningful Maven or Gradle setup

### 2. Standard Build Commands: 1.5 points

Evaluate documented commands for:

- build or compile
- test
- package
- run

Scoring:

- `1.5`: all four commands are documented and work from a clean terminal
- `1`: two or three commands work
- `0.5`: commands are present but mostly incomplete, inaccurate, or require manual correction
- `0`: fewer than two required commands work

### 3. Test Integration: 1 point

- `1`: tests run through Maven or Gradle without manual classpath setup
- `0.5`: test execution is mostly manual, skipped without clear reason, or IDE-dependent
- `0`: no meaningful build-tool test integration

### 4. Logging: 1.5 points

Required logged event types:

- game start
- player turn
- card played
- card drawn
- invalid input
- round or game end

Scoring:

- `1.5`: all required event types are logged clearly using a normal Java logging approach
- `1`: logging exists but covers only some important events
- `0.5`: minimal logging exists, but it is mostly ad-hoc or diagnostically weak
- `0`: no meaningful logging

Logging must not replace normal player-facing CLI output.

### 5. Docker: 1.5 points

- `1.5`: `Dockerfile` builds the application from repository contents and the documented Docker run command starts the game
- `1`: Dockerfile exists but build or run behavior needs minor manual correction
- `0.5`: Docker support is present but mostly incomplete or placeholder-like
- `0`: no meaningful Docker support

Docker must not depend on files outside the repository.

### 6. README Command Documentation: 1 point

Required README commands:

- local build
- local test
- local run
- package creation
- Docker build
- Docker run

Scoring:

- `1`: README includes exact commands for all required local and Docker operations, and they match the submitted project
- `0.5`: README has some useful command documentation but misses several required commands
- `0`: no useful README command documentation

## Grade Calculation

```text
Maven Or Gradle Project Setup /1.5
+ Standard Build Commands /1.5
+ Test Integration /1
+ Logging /1.5
+ Docker /1.5
+ README Command Documentation /1
= Assignment 4 grade /8
```

## Grading Summary Format

```text
Maven Or Gradle Project Setup: __/1.5
Standard Build Commands: __/1.5
Test Integration: __/1
Logging: __/1.5
Docker: __/1.5
README Command Documentation: __/1
Assignment 4 grade: __/8
```
