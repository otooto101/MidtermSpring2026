# Final Project UNO Rules Reference

## Purpose

This document defines the UNO-like rule behavior used as the reference for the final project feature menu.

You can implement any subset of these features and earn the corresponding rubric points in `Final_rubric.md`. If your submission intentionally implements a different rule variant, document that variant in `docs/rules-supported.md`.

## Supplemental External References

Use this local document as the grading reference.

For a visual explanation with card illustrations, see:

- [Original Uno Rules](https://www.unorules.com/)

For the official Mattel instruction sheet, see:

- [UNO Basic Instructions PDF](https://service.mattel.com/instruction_sheets/UNO%20Basic%20IS.pdf)

## Deck Composition

A classic UNO-style deck contains:

- four colors: red, yellow, green, blue
- one `0` card in each color
- two cards for each number `1-9` in each color
- two `Skip` cards in each color
- two `Reverse` cards in each color
- two `Draw Two` cards in each color
- four `Wild` cards
- four `Wild Draw Four` cards

Total: `108` cards.

## Basic Turn Flow

1. Each player starts with a hand of cards.
2. One card is placed face-up as the discard/top card.
3. On a player's turn, the player may play a legal card.
4. If the player cannot play, the player draws a card.
5. If the drawn card is legal, the implementation may allow the player to play it immediately.
6. If the player does not play, the turn passes to the next player.

If the starting discard card is an action card or wild card, the implementation may either apply the effect or redraw a normal starting card. The chosen behavior should be documented.

## Legal Play Validation

A card is legal when at least one of these is true:

- its color matches the current active color
- its number matches the top card number
- its action type matches the top card action type
- it is a `Wild`
- it is a `Wild Draw Four`

After a wild card is played, the selected color becomes the active color for future legal-play checks.

## Skip

When `Skip` is played:

- the next player loses their turn
- play continues with the player after the skipped player

## Reverse

When `Reverse` is played:

- turn direction changes from clockwise to counterclockwise, or the reverse
- in a two-player game, `Reverse` may be treated like `Skip`

The two-player behavior should be documented.

## Draw Two

When `Draw Two` is played:

- the next player draws two cards
- the next player loses their turn
- play continues with the following player

Stacking Draw Two cards is a variant rule. If implemented, document it clearly.

## Wild

When `Wild` is played:

- the player chooses the next active color
- future legal-play checks use that chosen color
- the next player takes a normal turn

## Wild Draw Four

When `Wild Draw Four` is played:

- the player chooses the next active color
- the next player draws four cards
- the next player loses their turn
- play continues with the following player

Challenge rules for Wild Draw Four are optional. If implemented, document them clearly.

## Draw And Pass Behavior

The project may use either of these common draw/pass variants:

- draw one card, then play it immediately if legal, otherwise pass
- draw one card and pass without playing it

The chosen behavior should be documented and tested if the feature is implemented.

## UNO Call And Missed-UNO Penalty

When a player has one card left:

- the game should be able to detect the one-card state
- the player may call UNO
- if the player fails to call UNO before the next relevant action, a penalty may be applied

A common penalty is drawing two cards.

The exact timing rule for detecting a missed UNO call should be documented.

## Round End

A round ends when a player has no cards left.

The player who empties their hand wins the round.

## Scoring

At the end of a round, the round winner receives points for cards remaining in other players' hands.

Common card values:

- number cards: face value
- `Skip`: `20`
- `Reverse`: `20`
- `Draw Two`: `20`
- `Wild`: `50`
- `Wild Draw Four`: `50`

## Multi-Round Game Target

The game may continue across multiple rounds until a target score is reached.

A common target is `500` points.

The final winner is the player who reaches or exceeds the target score first.

## Acceptable Simplifications

The final project is point-based. A submission may still earn credit without implementing every rule above.

Acceptable simplifications include:

- no Wild Draw Four challenge rule
- no draw-card stacking
- simple bot behavior
- text-only CLI interaction
- fixed target score
- deterministic deck setup for tests

Any simplification that affects visible gameplay should be documented in `docs/rules-supported.md`.
