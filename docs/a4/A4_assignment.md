# Assignment 4: Build Tools, Logging, And Docker For UNO

## Context

Continue from your midterm UNO CLI project.

The goal of Assignment 4 is to make the project easier to build, test, package, run, and diagnose.

This assignment is not about adding new UNO rules. It is about project infrastructure.

## Main Goal

Convert your UNO project into a standard Java project with:

- Maven or Gradle
- automated test execution through the build tool
- logging for important game events
- Docker support
- clear run/build/test documentation

## Required Work

### 1. Build Tool

Use either Maven or Gradle.

Your repository must include one of:

- `pom.xml`
- `build.gradle`
- `build.gradle.kts`

The project must support standard commands for:

- compiling/building the project
- running tests
- packaging the application
- running the application

### 2. Test Integration

Your existing tests must run through the build tool.

Examples:

- `mvn test`
- `gradle test`
- `./gradlew test`

Tests should not require manual classpath setup.

### 3. Logging

Add logging using a normal Java logging approach.

Acceptable examples:

- `java.util.logging`
- Logback
- Log4j
- SLF4J with a backend

Log at least these events:

- game start
- player turn
- card played
- card drawn
- invalid input
- round or game end

Do not replace normal user-facing CLI output with logs. The CLI should still be readable for players.

### 4. Docker

Add a `Dockerfile` that builds and runs the application.

The Docker setup must allow the game to start from a documented command.

If your project uses a wrapper such as `mvnw` or `gradlew`, include the files needed for Docker to use it.

### 5. README

Add or update `README.md`.

It must include exact commands for:

- local build
- local test
- local run
- package creation
- Docker build
- Docker run

## Deliverables

Submit:

- build tool configuration
- updated source code if needed for build structure
- tests runnable through the build tool
- logging implementation
- `Dockerfile`
- `README.md`

## Constraints

- Do not rewrite the game.
- Do not remove existing tests.
- Do not hide build failures behind scripts that ignore errors.
- Do not require IDE-specific steps.
- Do not log sensitive local paths or machine-specific data.
- Do not make Docker depend on files outside the repository.

## Suggested Workflow

1. Choose Maven or Gradle.
2. Move source and test files into the standard project layout if needed.
3. Make the test command pass.
4. Add packaging and run commands.
5. Add logging.
6. Add Docker support.
7. Verify the README commands from a clean terminal.

