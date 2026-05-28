# Extension Readiness Note

## Which extension would this design support best?

**Replacing or improving the CLI view** — this is the easiest extension by far.

A close second: **adding a smarter bot strategy**.

## Where would the change be implemented?

### Replace the CLI view

`Display.java` now owns all terminal output. Every game event has a named method:
`Display.playerPlays()`, `Display.drawsTwo()`, `Display.winsRound()`, etc.

To swap the CLI for a different output format (JSON log, colour ANSI output, web socket stream):
- Create a new class, e.g. `JsonDisplay`, with the same method signatures
- Point `Main` at the new class
- `Main.java` and the game rules do not change at all

The `quiet` flag in Main is the only remaining coupling — it would move into the Display implementation.

### Add a smarter bot strategy

`chooseBotCard(hand)` and `chooseBotColor(hand)` are isolated methods that receive a hand and read `state.upCard` / `state.calledColor`. They do not touch the game loop.

To add a smarter strategy (e.g. count cards, prefer cards that leave more options):
- The signature stays the same
- The method body is replaced or a strategy object is passed in
- No other code changes

### Add a rule variant

`Card.isLegalOn()` in `Card.java` is the single place that defines what a legal play is. A rule variant (e.g. stacking draw twos) would require changing only this method and adding a test.

`applyCardEffect()` in `Main.java` is the single place that defines what each card does after being played. A new card type or effect (e.g. swap hands) adds one `case` to the switch.

## What part of the design still makes change difficult?

- **`GameState` is accessed directly** — `Main` methods reach into `state.upCard`, `state.calledColor` etc. directly. `chooseBotCard` reads game state through statics rather than receiving it as a parameter. A fully decoupled bot would receive a read-only view of the game state, not the mutable object itself.

- **`scores[]` is separate from `Player`** — if a player is added or removed between rounds, the score array index could drift. Putting `score` inside `Player` would fix this.

- **Turn orchestration is still one large `playGame()` method** — extracting a `TurnResult` or turn-state machine would make it easier to add features like undo, replay, or async turns.

- **`Display` reads `Main.quiet` directly** — Display is not fully independent. Passing `quiet` as a constructor argument or through a config object would decouple it completely.

