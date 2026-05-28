# Refactoring Report

## What behavior did I characterize before refactoring?

Before touching any code I wrote `CharacterizationTest.java` — 91 tests covering:

- Card parsing: `color()`, `rank()`, `number()`, `points()` on every card type
- Legality: matching by color, number, action type, wild, called color after wild, illegal combos
- Bot card selection priority: DRAW_TWO → SKIP → NUMBER → WILD
- Bot color selection including the tie-break quirk (R wins when R and B are equal because R is checked first in the if-chain)
- Scoring: numbers = face value, action cards = 20, wilds = 50
- Card effects in-game: skip actually skips a player, reverse flips direction, draw two adds 2 cards and skips a turn
- Deck recycling: discard is reshuffled into deck when deck runs out
- Edge case: `number("Y")` throws `NumberFormatException` because `rank()` falls through to NUMBER and then `parseInt("")` crashes — this is a known fragility in the original code

All 91 tests were committed before any refactoring started so the git history proves tests preceded changes.

## What were the worst design problems?

Looking at the original `Main.java` against `docs/expected-smells.md`:

1. **Duplicated legality checks** — the same 5-line legality block appeared copy-pasted 3 times inside `chooseBotCard` and again inline in the game loop. Any fix to legality rules required 4 changes.

2. **Mixed CLI and game logic** — `System.out.println` calls were scattered through the game loop, `applyCardEffect`, `scoreRound`, `handleDraw`. Impossible to test rules silently.

3. **Primitive card representation** — `color()`, `rank()`, `number()`, `points()`, `isLegal()` were all static string-parsing methods floating in `Main`. Card knowledge had no home.

4. **Global mutable state** — 10+ static fields in `Main` meant any test had to carefully save and restore state manually and any future multi-game parallelism would be impossible.

5. **Weak player boundaries** — players were three parallel arrays (`playerNames`, `humanPlayers`, `hands`). Accessing a player required three separate index lookups and staying in sync.

6. **Hidden randomness** — `Random` was a static field, making tests non-deterministic unless `--seed` was passed at the CLI level.

## Which refactorings did I perform?

Each step was a separate commit with tests passing before and after:

1. `chooseBotCard` — replaced 3 copy-pasted legality blocks with `isLegal()` calls
2. Extracted `applyCardEffect(card)` — isolated the skip/reverse/draw-two/wild-draw-four if-chain
3. Extracted `scoreRound(name)` — winning detection and point calculation out of the loop
4. Extracted `buildDeck()` — deck construction out of `playGame()`
5. Extracted `dealHands()` — dealing 7 cards out of `playGame()`
6. Extracted `handleDraw(hand, name)` — draw-card logic out of the game loop
7. Introduced `Display` class — all `System.out.println` calls moved out of game logic; `if (!quiet)` guards live in Display only
8. Introduced `Card` value object — `color()`, `rank()`, `number()`, `points()`, `isLegalOn()` moved to Card; Main delegates with one-liners
9. Replaced inline legality block in game loop with `isLegal()` call
10. Extracted `parseCardInput()` from `askHuman()` — input parsing separated from legality validation
11. Introduced `GameState` — all 10 mutable game fields moved out of Main statics
12. Moved `Random` into `GameState` — randomness is now part of game state, controllable per session
13. Introduced `Player` class — replaced three parallel arrays with one `ArrayList<Player>`
14. Converted `applyCardEffect` if-else chain to a `switch` statement

## What behavior did I intentionally preserve?

Everything from `docs/rules.html` quirks section:

- All hands are printed on every turn (not hidden)
- Human can type `draw` even when holding a legal card
- Typing an index for an illegal card causes a penalty card and lost turn (not re-prompt)
- Typing a card code for an illegal card causes a re-prompt (not penalty)
- Bot players automatically play a drawn card when it is legal
- Reverse on a 2-player game acts like a skip (next() is called twice)
- Deck is recycled from discard when empty

The bot tie-break quirk is also preserved: when two colors are tied, R wins because the if-chain checks R first. This is documented in the characterization tests.

## What risks remain?

- **`scores[]` is still an int array indexed by player position** — inconsistent with the Player object. A player removed mid-game would shift indices.
- **`chooseBotColor` verbose if-chain** — still four variables and four comparisons; could be a map lookup.
- **`join()` string concatenation in loop** — minor performance smell, not a correctness risk.
- **No network or persistence layer** — adding save/load would require further GameState work.
- **`number("Y")` crash still exists in Card** — the fragility moved from Main to Card but is not fixed. Any code path that constructs a Card with a malformed string and calls `number()` will throw.

