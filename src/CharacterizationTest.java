import java.util.ArrayList;

// Characterization tests - these pin down what the code actually does right now,
// before I start refactoring. Not testing ideal UNO, testing THIS code.
// Run: java -cp out CharacterizationTest
public class CharacterizationTest {

    static int passed = 0;
    static int failed = 0;
    static int total  = 0;

    // p.s i havent used testng or junit. i wrote this testing with pure java
    public static void main(String[] args) {
        System.out.println("=== Characterization Tests ===\n");

        suite("color()",          CharacterizationTest::testColor);
        suite("rank()",           CharacterizationTest::testRank);
        suite("number()",         CharacterizationTest::testNumber);
        suite("points()",         CharacterizationTest::testPoints);
        suite("isLegal – color",  CharacterizationTest::testIsLegalByColor);
        suite("isLegal – number", CharacterizationTest::testIsLegalByNumber);
        suite("isLegal – action", CharacterizationTest::testIsLegalByAction);
        suite("isLegal – wild",   CharacterizationTest::testIsLegalWild);
        suite("isLegal – called color", CharacterizationTest::testIsLegalCalledColor);
        suite("isLegal – illegal combos", CharacterizationTest::testIsLegalIllegal);
        suite("Bot card priority: DRAW_TWO first", CharacterizationTest::testBotPrefersDraw2);
        suite("Bot card priority: SKIP second",    CharacterizationTest::testBotPrefersSkip);
        suite("Bot card priority: NUMBER before WILD", CharacterizationTest::testBotPrefersNumberBeforeWild);
        suite("Bot plays WILD when only option",   CharacterizationTest::testBotPlayWildWhenOnly);
        suite("Bot returns -1 when no legal card", CharacterizationTest::testBotReturnsMinusOne);
        suite("Bot color: picks most in hand",     CharacterizationTest::testBotColorMajority);
        suite("Bot color: tie breaks to first max",CharacterizationTest::testBotColorTie);
        suite("Scoring – numbers",                 CharacterizationTest::testScoringNumbers);
        suite("Scoring – action cards",            CharacterizationTest::testScoringActions);
        suite("Scoring – wilds",                   CharacterizationTest::testScoringWilds);
        suite("Edge: illegal input index behavior",CharacterizationTest::testIllegalIndexBehavior);
        suite("Edge: empty drawn card play check", CharacterizationTest::testDrawnCardLegalCheck);
        suite("Edge: reverse on 2-player acts like skip", CharacterizationTest::testReverse2Player);
        suite("Edge: draw from empty deck recycles discard", CharacterizationTest::testDeckRecycle);
        suite("Effect: skip actually skips next player", CharacterizationTest::testSkipEffect);
        suite("Effect: reverse changes direction", CharacterizationTest::testReverseEffect);
        suite("Effect: draw two adds 2 cards to next player", CharacterizationTest::testDrawTwoEffect);

        System.out.println("\n==============================");
        System.out.printf("TOTAL: %d passed, %d failed out of %d%n", passed, failed, total);
        System.out.println("==============================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // color() just reads the first character
    static void testColor() {
        check("R5 → R",  Main.color("R5").equals("R"));
        check("Y0 → Y",  Main.color("Y0").equals("Y"));
        check("GS → G",  Main.color("GS").equals("G"));
        check("BR → B",  Main.color("BR").equals("B"));
        check("W  → ''", Main.color("W").equals(""));
        check("W4 → ''", Main.color("W4").equals(""));
        check("B+2 → B", Main.color("B+2").equals("B"));
    }

    // rank() looks at the suffix
    static void testRank() {
        check("W  → WILD",           Main.rank("W").equals("WILD"));
        check("W4 → WILD_DRAW_FOUR", Main.rank("W4").equals("WILD_DRAW_FOUR"));
        check("RS → SKIP",           Main.rank("RS").equals("SKIP"));
        check("GS → SKIP",           Main.rank("GS").equals("SKIP"));
        check("YR → REVERSE",        Main.rank("YR").equals("REVERSE"));
        check("BR → REVERSE",        Main.rank("BR").equals("REVERSE"));
        check("R+2 → DRAW_TWO",      Main.rank("R+2").equals("DRAW_TWO"));
        check("G+2 → DRAW_TWO",      Main.rank("G+2").equals("DRAW_TWO"));
        check("R5 → NUMBER",         Main.rank("R5").equals("NUMBER"));
        check("B0 → NUMBER",         Main.rank("B0").equals("NUMBER"));
        check("Y9 → NUMBER",         Main.rank("Y9").equals("NUMBER"));
    }

    // number() returns -1 for anything that isn't a number card
    static void testNumber() {
        check("R0 → 0", Main.number("R0") == 0);
        check("G7 → 7", Main.number("G7") == 7);
        check("Y9 → 9", Main.number("Y9") == 9);
        check("B1 → 1", Main.number("B1") == 1);
        check("RS → -1",  Main.number("RS") == -1);
        check("W  → -1",  Main.number("W")  == -1);
        check("W4 → -1",  Main.number("W4") == -1);
    }

    static void testPoints() {
        check("R0  → 0",  Main.points("R0")  == 0);
        check("G7  → 7",  Main.points("G7")  == 7);
        check("Y9  → 9",  Main.points("Y9")  == 9);
        check("RS  → 20", Main.points("RS")  == 20);
        check("YR  → 20", Main.points("YR")  == 20);
        check("B+2 → 20", Main.points("B+2") == 20);
        check("W   → 50", Main.points("W")   == 50);
        check("W4  → 50", Main.points("W4")  == 50);
    }

    static void testIsLegalByColor() {
        check("R2 on R9 (same color)",  Main.isLegal("R2",  "R9",  ""));
        check("RS on R5 (same color)",  Main.isLegal("RS",  "R5",  ""));
        check("R+2 on RR (same color)", Main.isLegal("R+2", "RR",  ""));
        check("R0 on R0 (same color)",  Main.isLegal("R0",  "R0",  ""));
        check("YS on YR (same color)",  Main.isLegal("YS",  "YR",  ""));
    }

    static void testIsLegalByNumber() {
        check("G9 on R9 (same number, diff color)", Main.isLegal("G9", "R9", ""));
        check("B5 on Y5 (same number)",             Main.isLegal("B5", "Y5", ""));
        check("R0 on G0 (same number 0)",           Main.isLegal("R0", "G0", ""));
        check("G3 on R9 (diff color+number) illegal", !Main.isLegal("G3", "R9", ""));
    }

    static void testIsLegalByAction() {
        check("RS on GS (skip matches skip)",           Main.isLegal("RS",  "GS",  ""));
        check("YR on BR (reverse matches reverse)",     Main.isLegal("YR",  "BR",  ""));
        check("B+2 on G+2 (draw two matches draw two)", Main.isLegal("B+2", "G+2", ""));
        check("RS on G+2 (skip vs draw2) illegal", !Main.isLegal("RS", "G+2", ""));
    }

    // wilds are always playable
    static void testIsLegalWild() {
        check("W on R5",   Main.isLegal("W",  "R5",  ""));
        check("W on BS",   Main.isLegal("W",  "BS",  ""));
        check("W4 on G3",  Main.isLegal("W4", "G3",  ""));
        check("W4 on YR",  Main.isLegal("W4", "YR",  ""));
        check("W on W (called R)", Main.isLegal("W",  "W",  "R"));
        check("W4 on W4 (called B)", Main.isLegal("W4", "W4", "B"));
    }

    // after a wild is played the called color is what matters, not the card's color
    static void testIsLegalCalledColor() {
        check("R3 on W, called R → legal",  Main.isLegal("R3",  "W", "R"));
        check("RS on W, called R → legal",  Main.isLegal("RS",  "W", "R"));
        check("B3 on W, called R → illegal",!Main.isLegal("B3",  "W", "R"));
        check("G9 on W, called R → illegal",!Main.isLegal("G9",  "W", "R"));
        check("W on W, called R → legal",   Main.isLegal("W",   "W", "R"));
        check("W4 on W, called B → legal",  Main.isLegal("W4",  "W", "B"));
    }

    static void testIsLegalIllegal() {
        check("B3 on R9 (color+number mismatch)",    !Main.isLegal("B3",  "R9",  ""));
        check("GS on R9 (color mismatch, diff rank)", !Main.isLegal("GS",  "R9",  ""));
        check("Y+2 on RS (action mismatch)",          !Main.isLegal("Y+2", "RS",  ""));
    }

    // bot prefers DRAW_TWO > SKIP > NUMBER > WILD (hardcoded priority passes)
    static void testBotPrefersDraw2() {
        ArrayList<String> hand = hand("R5", "R+2", "RS", "W");
        Main.upCard = "R9";
        Main.calledColor = "";
        int idx = Main.chooseBotCard(hand);
        check("bot picks R+2 (DRAW_TWO) first", hand.get(idx).equals("R+2"));
    }

    static void testBotPrefersSkip() {
        ArrayList<String> hand = hand("R5", "RS", "W");
        Main.upCard = "R9";
        Main.calledColor = "";
        int idx = Main.chooseBotCard(hand);
        check("bot picks RS (SKIP) before R5 or W", hand.get(idx).equals("RS"));
    }

    static void testBotPrefersNumberBeforeWild() {
        ArrayList<String> hand = hand("B3", "R5", "W");
        Main.upCard = "R9";
        Main.calledColor = "";
        int idx = Main.chooseBotCard(hand);
        check("bot picks R5 (NUMBER) before W", hand.get(idx).equals("R5"));
    }

    static void testBotPlayWildWhenOnly() {
        ArrayList<String> hand = hand("B3", "G2", "W");
        Main.upCard = "R9";
        Main.calledColor = "";
        int idx = Main.chooseBotCard(hand);
        check("bot plays W when it's the only legal card", hand.get(idx).equals("W"));
    }

    static void testBotReturnsMinusOne() {
        ArrayList<String> hand = hand("B3", "G2", "YS");
        Main.upCard = "R9";
        Main.calledColor = "";
        int idx = Main.chooseBotCard(hand);
        check("bot returns -1 when no legal card", idx == -1);
    }

    static void testBotColorMajority() {
        ArrayList<String> hand = hand("B1", "B2", "B3", "R5", "G7");
        check("bot picks B (3 vs 1 vs 1)", Main.chooseBotColor(hand).equals("B"));
    }

    // noticed that when two colors tie, R wins because it's checked first in the if-chain
    static void testBotColorTie() {
        ArrayList<String> hand = hand("R1", "R2", "B3", "B4");
        String color = Main.chooseBotColor(hand);
        check("bot picks R when R and B tied (current behavior)", color.equals("R"));
    }

    static void testScoringNumbers() {
        check("R0 = 0 pts",  Main.points("R0") == 0);
        check("Y1 = 1 pt",   Main.points("Y1") == 1);
        check("G5 = 5 pts",  Main.points("G5") == 5);
        check("B9 = 9 pts",  Main.points("B9") == 9);
    }

    static void testScoringActions() {
        check("RS  = 20 pts", Main.points("RS")  == 20);
        check("GR  = 20 pts", Main.points("GR")  == 20);
        check("Y+2 = 20 pts", Main.points("Y+2") == 20);
    }

    static void testScoringWilds() {
        check("W  = 50 pts", Main.points("W")  == 50);
        check("W4 = 50 pts", Main.points("W4") == 50);
    }

    // found this while testing: if you pass a single color letter like "Y",
    // rank() falls through to NUMBER and then parseInt("") blows up.
    // the game never does this in practice but it's a fragile spot worth noting.
    static void testIllegalIndexBehavior() {
        check("empty string color is ''", Main.color("").equals(""));
        check("color of 'W5' is ''", Main.color("W5").equals(""));

        boolean threw = false;
        try {
            Main.number("Y"); // rank("Y") = NUMBER, parseInt("") = crash
        } catch (NumberFormatException e) {
            threw = true;
        }
        check("number('Y') throws NumberFormatException", threw);
    }

    // drawn card is checked with isLegal too — bot auto-plays if legal, human gets asked
    static void testDrawnCardLegalCheck() {
        check("drawn R5 is legal on R9", Main.isLegal("R5", "R9", ""));
        check("drawn B3 is not legal on R9", !Main.isLegal("B3", "R9", ""));
    }

    // reverse flips direction; on 2 players next() is called twice so it skips like a skip
    static void testReverse2Player() {
        Main.direction = 1;
        Main.direction = Main.direction * -1;
        check("direction flips from 1 to -1", Main.direction == -1);
        Main.direction = Main.direction * -1;
        check("direction flips back to 1",    Main.direction == 1);
    }

    // when the deck runs out, discard gets reshuffled back in
    static void testDeckRecycle() {
        ArrayList<String> savedDeck    = Main.deck;
        ArrayList<String> savedDiscard = Main.discard;

        Main.deck    = new ArrayList<>();
        Main.discard = new ArrayList<>();
        Main.discard.add("R5");
        Main.discard.add("G3");

        String drawn = Main.draw();
        check("can draw from recycled discard", drawn != null && !drawn.isEmpty());
        check("discard is cleared after recycle", Main.discard.size() == 0);

        Main.deck    = savedDeck;
        Main.discard = savedDiscard;
    }

    // skip calls next() twice so player 1 gets completely skipped
    static void testSkipEffect() {
        int savedPlayer    = Main.currentPlayer;
        int savedDirection = Main.direction;
        ArrayList<String> savedNames = Main.playerNames;

        Main.playerNames = new ArrayList<>();
        Main.playerNames.add("A");
        Main.playerNames.add("B");
        Main.playerNames.add("C");
        Main.currentPlayer = 0;
        Main.direction = 1;

        Main.next();
        Main.next();

        check("skip: player 0 plays, player 1 skipped, lands on 2", Main.currentPlayer == 2);

        Main.currentPlayer = savedPlayer;
        Main.direction     = savedDirection;
        Main.playerNames   = savedNames;
    }

    // reverse flips direction; with 3 players moving backwards from 1 lands on 0
    static void testReverseEffect() {
        int savedPlayer    = Main.currentPlayer;
        int savedDirection = Main.direction;
        ArrayList<String> savedNames = Main.playerNames;

        Main.playerNames = new ArrayList<>();
        Main.playerNames.add("A");
        Main.playerNames.add("B");
        Main.playerNames.add("C");
        Main.currentPlayer = 1;
        Main.direction = 1;

        Main.direction = Main.direction * -1;
        Main.next();

        check("reverse: direction is now -1", Main.direction == -1);
        check("reverse: next player is 0 (moved backwards from 1)", Main.currentPlayer == 0);

        Main.currentPlayer = savedPlayer;
        Main.direction     = savedDirection;
        Main.playerNames   = savedNames;
    }

    // draw two: next player gets 2 extra cards and their turn is skipped
    static void testDrawTwoEffect() {
        int savedPlayer    = Main.currentPlayer;
        int savedDirection = Main.direction;
        ArrayList<String> savedNames = Main.playerNames;
        ArrayList<ArrayList<String>> savedHands = Main.hands;
        ArrayList<String> savedDeck = Main.deck;

        Main.playerNames = new ArrayList<>();
        Main.playerNames.add("A");
        Main.playerNames.add("B");
        Main.direction = 1;
        Main.currentPlayer = 0;

        Main.hands = new ArrayList<>();
        Main.hands.add(new ArrayList<>());
        Main.hands.add(new ArrayList<>());

        Main.deck = new ArrayList<>();
        Main.deck.add("R1");
        Main.deck.add("G3");
        Main.deck.add("B5");

        int before = Main.hands.get(1).size();

        // mirrors exactly what the game loop does for DRAW_TWO
        Main.next();
        Main.hands.get(Main.currentPlayer).add(Main.draw());
        Main.hands.get(Main.currentPlayer).add(Main.draw());
        Main.next();

        check("draw two: player 1 got 2 cards", Main.hands.get(1).size() == before + 2);
        check("draw two: ends back at player 0 (B's turn skipped)", Main.currentPlayer == 0);

        Main.currentPlayer = savedPlayer;
        Main.direction     = savedDirection;
        Main.playerNames   = savedNames;
        Main.hands         = savedHands;
        Main.deck          = savedDeck;
    }

    static ArrayList<String> hand(String... cards) {
        ArrayList<String> h = new ArrayList<>();
        for (String c : cards) h.add(c);
        return h;
    }

    @FunctionalInterface
    interface TestBlock { void run(); }

    static void suite(String name, TestBlock block) {
        System.out.println("  [" + name + "]");
        block.run();
        System.out.println();
    }

    static void check(String description, boolean condition) {
        total++;
        if (condition) {
            System.out.println("    PASS  " + description);
            passed++;
        } else {
            System.out.println("    FAIL  " + description);
            failed++;
        }
    }
}

