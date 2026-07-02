# Final Project: Full UNO Product

## Context

The final project continues the UNO work from the midterm.

Your goal is to turn the project into a more complete, maintainable UNO application.

## Main Goal

Deliver a working UNO project with:

- fuller UNO rules
- tested game logic
- playable CLI flow
- clear rule organization
- usable documentation

## UNO Rule Feature Menu

Implement the game as close to normal UNO rules as reasonable for this course project.

A local rule reference is provided in `Final_Project_UNO_rules_reference.md`.

You earn points for each rule feature you implement well. No single rule feature is a prerequisite for submitting the project.

- correct deck composition
- legal play validation
- Skip
- Reverse
- Draw Two
- Wild
- Wild Draw Four
- draw/pass behavior
- UNO call and missed-UNO penalty
- round scoring
- multi-round game to target score

## Product Quality Menu

You also earn points for product quality areas. No single quality feature is a prerequisite for submitting the project, but stronger submissions will combine several areas into a coherent game.

### 1. Game Architecture

Game rules and state can earn architecture credit when they are testable without console input.

The CLI should not be the only place where rules exist.

### 2. CLI Playability

The game should be playable from the command line without needing to understand the source code.

### 3. Tests

Include tests for:

- card legality
- action cards
- wild cards
- draw/pass flow
- scoring
- game-over or target-score behavior

## Deliverables

Submit:

- source code
- tests
- `README.md`
- `docs/rules-supported.md`
- `docs/final-report.md`

## Final Report

`docs/final-report.md` should explain:

- what UNO rules are implemented
- how the game is played from the CLI
- how the architecture separates game logic from CLI interaction
- what tests were added
- what limitations remain

`docs/rules-supported.md` should list which rules from `Final_Project_UNO_rules_reference.md` are implemented and which variants or simplifications are used.

## Constraints

- Do not replace the project with an unrelated game.
- Do not require undocumented machine-specific setup.
- Do not hide failing tests.
