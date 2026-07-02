# Final Project Report

## What UNO Rules Are Implemented

All ten feature-menu items from `Final_Project.md` are implemented:

- correct 108-card deck composition
- full legal-play validation (color, number, action type, wild, called color)
- Skip, Reverse (with documented 2-player = Skip variant), Draw Two
- Wild and Wild Draw Four, including color-calling
- draw-then-play-if-legal draw/pass behavior
- UNO call detection and a missed-call penalty (draw 2), checked at the start
  of the offending player's next turn
- round scoring by card point value, plus **two** game-length modes: a fixed
  `--games N` count (original) and a new `--target N` multi-round mode that
  plays until a player's cumulative score reaches the target, then reports
  an overall winner

See `docs/rules-supported.md` for the rule-by-rule breakdown and every
documented simplification (no Draw Two stacking, no Wild Draw Four challenge,
bots always call UNO automatically).

## How The Game Is Played From The CLI

Run via the packaged jar or `java -cp target/classes Main [flags]`:

```
--bots N        number of bot players (default 3)
--human         add a human-controlled player named "You"
--games N       play exactly N rounds then stop (default mode)
--target N      play rounds continuously until a player's score reaches N
--seed N        deterministic RNG seed, for reproducible games/tests
--quiet         suppress per-turn console narration (final scores still print)
--stats         print aggregate stats from persisted game history and exit
--self-test     run a small built-in smoke check and exit
```

Each turn, the current up card, called color (if any), and the active
player's hand are printed. A human player is prompted to choose a card by
index or by its code (e.g. `R5`, `W4`), or type `DRAW`. Illegal choices are
rejected with a message and the loop asks again rather than crashing. When a
human is left with one card, they are asked whether to call UNO. At the end
of a round, the winner and points scored are printed; at the end of the game
(or when the target score is reached), final scores and the overall winner
are printed.

## How The Architecture Separates Game Logic From CLI Interaction

- **`Card`** — pure value object; owns color/rank/number/points/legality logic.
  No I/O, fully unit-testable.
- **`Player`** — holds a player's name, human/bot flag, hand, and UNO-call state.
- **`GameState`** — all mutable session state (players, deck, discard, scores,
  current player, direction, up card, called color). No I/O.
- **`Display`** — the *only* class that prints game narration to the player.
  Every message the CLI shows lives here, gated by `Main.quiet`.
- **`GameLog`** — structured `java.util.logging` events (turn, play, draw,
  invalid input, UNO penalty, round/game end) written to stderr, independent
  of `Display`'s stdout narration.
- **`GameRepository` / `PersistenceService` / `*Entity`** — JPA/Hibernate
  persistence layer for game history, completely separate from game rules.
- **`Main`** — orchestrates the turn loop and owns the only `Scanner` for
  human input; delegates all rule logic to `Card`/`GameState` and all output
  to `Display`.

Because `Card`'s legality/scoring logic and the turn-effect logic in
`applyCardEffect`/`checkUnoPenalty`/`hasReachedTarget` operate purely on
`GameState`/`Player`/`String` card codes, **all of it is unit-tested without
any console input** — `CharacterizationTest` builds `GameState` and `Player`
objects directly and calls these methods, never touching `System.in`.

## What Tests Were Added

`CharacterizationTest` (102 checks) covers, per the rubric's required areas:

- **card legality**: color/number/action/wild/called-color match and mismatch cases
- **action cards**: Skip, Reverse (including a full 2-player next()/next() flow
  proving the skip-equivalent behavior), Draw Two effect tests
- **wild cards**: Wild and Wild Draw Four legality and effect (4-card draw + skip)
- **draw/pass flow**: drawn-card legality check, bot auto-play vs. human prompt path
- **scoring**: number/action/wild point values, plus `scoreRound()` itself
  (asserts the round winner's score total after opponents' hands are summed)
- **UNO call and penalty**: penalty applied when not called, not applied when
  called, not applied above 1 card
- **game-over / target-score behavior**: `hasReachedTarget`, `finalWinnerName`
  tie/majority behavior

`PersistenceTest` (12 checks, separate from characterization tests) covers
the JPA repository layer independently.

All 114 tests pass via `mvn test`.

## What Limitations Remain

- No Draw Two stacking and no Wild Draw Four challenge rule (both explicitly
  optional per the rules reference).
- Bots use simple fixed-priority and majority-color heuristics rather than
  genuine strategy.
- `Main` still owns the single `Scanner` and CLI I/O for human turns directly,
  rather than behind an injectable input abstraction — this keeps the human
  turn path itself untested by `CharacterizationTest` (only the underlying
  rule methods it calls are tested).
- The missed-UNO penalty is checked only at the start of the same player's
  own next turn, not the moment another player could "catch" them mid-turn —
  a simpler, documented timing rule rather than the most aggressive one.


