# Rules Supported

This document maps every rule from `finalproject/Final_Project_UNO_rules_reference.md`
to what is actually implemented in this codebase, and documents every simplification.

## Deck Composition — Implemented

`Main.buildDeck()` builds the full 108-card classic deck:

- 4 colors (R, Y, G, B)
- one `0` per color, two of each `1`-`9` per color
- two `Skip`, two `Reverse`, two `Draw Two` per color
- four `Wild`, four `Wild Draw Four`

Tested in `CharacterizationTest` indirectly through `points()`/`rank()`/`color()`
unit checks on every card code produced by the deck.

## Legal Play Validation — Implemented

`Card.isLegalOn(upCode, calledColor)` covers:

- same color as the up card
- same number (number cards only)
- same action type (Skip/Reverse/Draw Two match their own kind)
- Wild and Wild Draw Four are always legal
- after a wild, the called color is used instead of the wild's own (colorless) color

Tested extensively in `CharacterizationTest` (`isLegal – color/number/action/wild/called color/illegal combos`).

## Skip — Implemented

`applyCardEffect()` calls `next()` twice, so the very next player is skipped entirely
and play resumes with the player after them. Works for any player count.

Tested in `testSkipEffect`.

## Reverse — Implemented

Direction is flipped (`state.direction *= -1`). For 3+ players this simply changes
turn order. **Simplification:** for exactly 2 players, Reverse is treated identically
to Skip (the same player is skipped) — this is the officially documented common
variant and is called out explicitly in the rules reference as acceptable.

Tested in `testReverseEffect`, `testReverse2Player`, and `testReverseTwoPlayerFullFlow`
(the last one drives the exact 2-player `next()`/`next()` sequence and asserts the
turn lands back on the same player, proving the skip-equivalent behavior end-to-end).

## Draw Two — Implemented

Next player draws 2 cards and their turn is skipped; play continues with the
following player. **Simplification:** stacking (playing a Draw Two on top of a
Draw Two to pass the penalty along) is **not implemented** — the drawing player
always takes the cards immediately.

Tested in `testDrawTwoEffect`.

## Wild — Implemented

Playing a Wild lets the player (human via prompt, bot via majority-color heuristic
in `chooseBotColor`) choose the next active color. That color feeds `isLegalOn`
for every subsequent play until changed again.

Tested in `isLegal – wild`, `isLegal – called color`, `Bot color` suites.

## Wild Draw Four — Implemented

Same color-choice behavior as Wild, plus the next player draws 4 cards and their
turn is skipped. **Simplification:** the "challenge" rule (accusing a player of
playing Wild Draw Four illegally) is **not implemented** — this is called out as
optional in the reference document.

Tested in `testWildDrawFourEffect`.

## Draw/Pass Behavior — Implemented

Chosen variant: **draw one card, then play it immediately if legal** (bots
auto-play a legal drawn card; humans are asked `y/n`). If the drawn card isn't
legal, or the human declines, the turn simply passes — there is no separate
"pass" action since drawing already consumes the turn when nothing is played.

Tested in `Edge: empty drawn card play check` and `testDrawnCardLegalCheck`.

## UNO Call And Missed-UNO Penalty — Implemented

- One-card state is detected the moment a play brings a hand down to size 1.
- **Bots always call UNO automatically** (documented simplification — no bot
  "forgetting" simulation).
- **Humans are prompted** (`Call UNO? y/n:`) at that exact moment.
- **Missed-call timing rule:** the penalty is checked at the very start of that
  same player's *next* turn (`Main.checkUnoPenalty`, called before `Display.turnState`
  in `playGame()`). If they are still sitting at exactly 1 card and never called
  UNO, they draw 2 penalty cards before acting.

Tested in `testUnoPenaltyApplied`, `testUnoPenaltyNotAppliedWhenCalled`,
`testUnoPenaltyIgnoredAboveOneCard`.

## Round Scoring And Multi-Round Target — Implemented

- `scoreRound()` sums the point value of every card left in every other player's
  hand and awards it to the round winner (`Card.points()`: number = face value,
  Skip/Reverse/Draw Two = 20, Wild/Wild Draw Four = 50). Directly tested in
  `testScoreRoundAwardsPoints`, which calls `Main.scoreRound()` against a
  constructed `GameState` and asserts the winner's score total.
- **Two supported game-length modes:**
  - `--games N` — play exactly N rounds, then stop and show final scores (original
    behavior, still supported for backward compatibility with earlier assignments).
  - `--target N` — play rounds continuously until any player's cumulative score
    reaches or exceeds `N`; the player with the highest score at that point is
    declared the overall winner (`Main.hasReachedTarget`, `Main.finalWinnerName`).
  - If neither is given, `--games 1` is the default.

Tested in `testHasReachedTarget`, `testFinalWinnerName`, and manually verified
end-to-end with `--target 150 --seed 42`.

## Other Acceptable Simplifications In Use

- Simple bot behavior: fixed priority order (Draw Two > Skip > Number > Wild),
  majority-color heuristic for wild color choice.
- Text-only CLI (`Display` class), no graphics.
- Deterministic deck setup available via `--seed N` for reproducible test games.
- Starting up-card: if a wild is drawn as the initial up card, it is discarded
  and redrawn until a non-wild card is found (documented in `playGame()`).



