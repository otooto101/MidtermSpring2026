# Final Project Rubric

## Point Value

The final project is worth:

- `60` rubric points

Points above `40` are dropped when recording the course grade:

```text
recorded final project grade = min(earned rubric points, 40)
```

This lets you choose which parts of the project to emphasize while keeping the recorded grade capped at `40`.

Course-level bonuses are defined separately in `A4_A5_Final_Project_evaluation_process.md`.

## 1. Fuller UNO Rules Implementation: 50 points

### 1.1 Correct Deck Composition: 5 points

Suggested split:

- deck composition behavior: `4`
- deck composition tests: `1`

Credit evidence:

- four colors
- numbered cards
- Skip cards
- Reverse cards
- Draw Two cards
- Wild cards
- Wild Draw Four cards

### 1.2 Legal Play Validation: 7 points

Suggested split:

- legal-play behavior: `5`
- legal-play tests: `2`

Credit evidence:

- match by color
- match by number
- match by action type
- wild cards playable according to game rules
- illegal plays rejected

### 1.3 Skip: 5 points

Suggested split:

- Skip behavior: `3`
- Skip tests: `2`

Credit evidence:

- next player loses turn
- behavior works in multi-player game flow

### 1.4 Reverse: 5 points

Suggested split:

- Reverse behavior: `3`
- Reverse tests: `2`

Credit evidence:

- play direction changes for three or more players
- two-player behavior is handled consistently and documented

### 1.5 Draw Two: 5 points

Suggested split:

- Draw Two behavior: `3`
- Draw Two tests: `2`

Credit evidence:

- next player draws two cards
- next player loses turn

### 1.6 Wild: 5 points

Suggested split:

- Wild behavior: `3`
- Wild tests: `2`

Credit evidence:

- player chooses next color
- chosen color affects legal play validation

### 1.7 Wild Draw Four: 5 points

Suggested split:

- Wild Draw Four behavior: `3`
- Wild Draw Four tests: `2`

Credit evidence:

- player chooses next color
- next player draws four cards
- next player loses turn

### 1.8 Draw/Pass Behavior: 5 points

Suggested split:

- draw/pass behavior: `3`
- draw/pass tests: `2`

Credit evidence:

- player can draw when drawing is applicable
- drawn card may be played if rules allow
- player can pass when no legal play is available
- behavior is documented if it differs from official UNO

### 1.9 UNO Call And Penalty: 4 points

Suggested split:

- UNO call and penalty behavior: `3`
- UNO call and penalty tests: `1`

Credit evidence:

- one-card state is detected
- player can call UNO
- missed UNO call can be penalized
- penalty is tested

### 1.10 Round Scoring And Multi-Round Target: 4 points

Suggested split:

- scoring and multi-round behavior: `3`
- scoring and multi-round tests: `1`

Credit evidence:

- remaining cards are scored
- round winner receives points
- multiple rounds continue until target score
- final winner is determined

## 2. Other Final Project Areas: 10 points

### 2.1 Game Design And Rule Organization: 4 points

Measured by:

- game logic testable without console input
- CLI separated from rule execution
- rule behavior has a clear home
- packages/classes have clear responsibilities

### 2.2 CLI Playability: 2 points

Measured by:

- players can complete a normal game from the CLI
- prompts and output are understandable
- invalid input is handled without crashing

### 2.3 Documentation And Final Report: 4 points

Measured by:

- clear README
- supported rules documented
- final report present and evidence-based

## Grade Calculation

```text
Fuller UNO Rules Implementation /50
+ Other Final Project Areas /10
= Earned rubric points /60

Recorded final project grade = min(earned rubric points, 40) /40
```

## Grading Summary Format

```text
Fuller UNO Rules Implementation: __/50
  Correct Deck Composition: __/5
  Legal Play Validation: __/7
  Skip: __/5
  Reverse: __/5
  Draw Two: __/5
  Wild: __/5
  Wild Draw Four: __/5
  Draw/Pass Behavior: __/5
  UNO Call And Penalty: __/4
  Round Scoring And Multi-Round Target: __/4

Other Final Project Areas: __/10
  Game Design And Rule Organization: __/4
  CLI Playability: __/2
  Documentation And Final Report: __/4

Earned rubric points: __/60
Final project grade: min(__, 40) = __/40
```
