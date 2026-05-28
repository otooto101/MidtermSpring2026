import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static GameState state = new GameState();
    static boolean quiet = false;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        state.random = new Random(seed);
        setupPlayers(bots, human);

        if (state.playerNames.size() < 2 || state.playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        for (int g = 1; g <= games; g++) {
            Display.gameStart(g);
            playGame();
        }

        Display.finalScores(state.playerNames, state.scores);
    }

    static void setupPlayers(int bots, boolean human) {
        state.playerNames.clear();
        state.humanPlayers.clear();
        state.hands.clear();
        if (human) {
            state.playerNames.add("You");
            state.humanPlayers.add(Boolean.TRUE);
            state.hands.add(new ArrayList<>());
        }
        for (int i = 1; i <= bots; i++) {
            state.playerNames.add("Bot" + i);
            state.humanPlayers.add(Boolean.FALSE);
            state.hands.add(new ArrayList<>());
        }
    }

    static void playGame() {
        buildDeck();
        Collections.shuffle(state.deck, state.random);
        state.discard.clear();
        dealHands();
        state.upCard = draw();
        while (state.upCard.startsWith("W")) {
            state.discard.add(state.upCard);
            state.upCard = draw();
        }
        state.calledColor = "";
        state.direction = 1;
        state.currentPlayer = state.random.nextInt(state.playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = state.playerNames.get(state.currentPlayer);
            ArrayList<String> hand = state.hands.get(state.currentPlayer);

            Display.turnState(state.upCard, state.calledColor, name, hand);

            int chosen = -1;
            if (state.humanPlayers.get(state.currentPlayer).booleanValue()) {
                chosen = askHuman(hand);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                chosen = handleDraw(hand, name);
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    Display.penaltyCard(name);
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);

                if (!isLegal(card, state.upCard, state.calledColor)) {
                    Display.illegalCard(name, card);
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                state.discard.add(state.upCard);
                state.upCard = card;
                state.calledColor = "";
                Display.playerPlays(name, card);

                if (card.equals("W") || card.equals("W4")) {
                    if (state.humanPlayers.get(state.currentPlayer).booleanValue()) {
                        state.calledColor = askColor();
                    } else {
                        state.calledColor = chooseBotColor(hand);
                    }
                    Display.playerCallsColor(name, state.calledColor);
                }

                if (hand.size() == 1) {
                    Display.uno(name);
                }

                if (hand.size() == 0) {
                    scoreRound(name);
                    return;
                }

                applyCardEffect(card);
            } else {
                next();
            }
        }
        Display.safetyLimit();
    }

    // draws a card for the current player and returns the index to play it,
    // or -1 if the drawn card isn't legal (or human chose not to play it)
    static int handleDraw(ArrayList<String> hand, String name) {
        String drawn = draw();
        hand.add(drawn);
        Display.playerDraws(name, drawn);
        if (isLegal(drawn, state.upCard, state.calledColor)) {
            if (!state.humanPlayers.get(state.currentPlayer).booleanValue()) {
                return hand.size() - 1;
            } else {
                System.out.print("Play drawn card " + drawn + "? y/n: ");
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                    return hand.size() - 1;
                }
            }
        }
        return -1;
    }

    static void dealHands() {
        for (int i = 0; i < state.hands.size(); i++) {
            state.hands.get(i).clear();
        }
        for (int i = 0; i < state.playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                state.hands.get(i).add(draw());
            }
        }
    }

    static void buildDeck() {
        state.deck.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            state.deck.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                state.deck.add(colors[c] + n);
                state.deck.add(colors[c] + n);
            }
            state.deck.add(colors[c] + "S");
            state.deck.add(colors[c] + "S");
            state.deck.add(colors[c] + "R");
            state.deck.add(colors[c] + "R");
            state.deck.add(colors[c] + "+2");
            state.deck.add(colors[c] + "+2");
        }
        for (int i = 0; i < 4; i++) {
            state.deck.add("W");
            state.deck.add("W4");
        }
    }

    static String draw() {
        if (state.deck.size() == 0) {
            state.deck.addAll(state.discard);
            state.discard.clear();
            Collections.shuffle(state.deck, state.random);
        }
        if (state.deck.size() == 0) {
            return "W";
        }
        return state.deck.remove(0);
    }

    static void scoreRound(String winnerName) {
        int points = 0;
        for (int i = 0; i < state.hands.size(); i++) {
            if (i != state.currentPlayer) {
                for (int j = 0; j < state.hands.get(i).size(); j++) {
                    points += points(state.hands.get(i).get(j));
                }
            }
        }
        state.scores[state.currentPlayer] += points;
        Display.winsRound(winnerName, points);
    }

    static void applyCardEffect(String card) {
        if (rank(card).equals("SKIP")) {
            next();
            next();
        } else if (rank(card).equals("REVERSE")) {
            state.direction = state.direction * -1;
            if (state.playerNames.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (rank(card).equals("DRAW_TWO")) {
            next();
            state.hands.get(state.currentPlayer).add(draw());
            state.hands.get(state.currentPlayer).add(draw());
            Display.drawsTwo(state.playerNames.get(state.currentPlayer));
            next();
        } else if (rank(card).equals("WILD_DRAW_FOUR")) {
            next();
            for (int i = 0; i < 4; i++) {
                state.hands.get(state.currentPlayer).add(draw());
            }
            Display.drawsFour(state.playerNames.get(state.currentPlayer));
            next();
        } else {
            next();
        }
    }

    static int chooseBotCard(ArrayList<String> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (rank(hand.get(i)).equals("DRAW_TWO") && isLegal(hand.get(i), state.upCard, state.calledColor)) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            if (rank(hand.get(i)).equals("SKIP") && isLegal(hand.get(i), state.upCard, state.calledColor)) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            if (rank(hand.get(i)).equals("NUMBER") && isLegal(hand.get(i), state.upCard, state.calledColor)) {
                return i;
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }
        return -1;
    }

    // just parsing — no legality check here, returns -1 for draw, -2 if not understood
    static int parseCardInput(String input, ArrayList<String> hand) {
        if (input.equals("DRAW")) return -1;
        try {
            int index = Integer.parseInt(input);
            if (index >= 0 && index < hand.size()) return index;
        } catch (Exception ignored) {
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).equals(input)) return i;
        }
        return -2;
    }

    static int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            int idx = parseCardInput(input, hand);
            if (idx == -1) return -1;
            if (idx == -2) {
                System.out.println("Card not found.");
                continue;
            }
            if (!isLegal(hand.get(idx), state.upCard, state.calledColor)) {
                System.out.println("That card is not legal.");
                continue;
            }
            return idx;
        }
    }

    static String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R")) return "R";
            if (input.equals("Y")) return "Y";
            if (input.equals("G")) return "G";
            if (input.equals("B")) return "B";
            System.out.println("Bad color.");
        }
    }

    static String chooseBotColor(ArrayList<String> hand) {
        int r = 0, y = 0, g = 0, b = 0;
        for (int i = 0; i < hand.size(); i++) {
            String c = color(hand.get(i));
            if (c.equals("R")) r++;
            else if (c.equals("Y")) y++;
            else if (c.equals("G")) g++;
            else if (c.equals("B")) b++;
        }
        if (r >= y && r >= g && r >= b) return "R";
        if (y >= r && y >= g && y >= b) return "Y";
        if (g >= r && g >= y && g >= b) return "G";
        return "B";
    }

    // these delegate to Card so the logic lives in one place
    static boolean isLegal(String card, String up, String call) { return new Card(card).isLegalOn(up, call); }
    static String color(String card)  { return new Card(card).color(); }
    static String rank(String card)   { return new Card(card).rank(); }
    static int    number(String card) { return new Card(card).number(); }
    static int    points(String card) { return new Card(card).points(); }

    static void next() {
        state.currentPlayer += state.direction;
        if (state.currentPlayer >= state.playerNames.size()) state.currentPlayer = 0;
        if (state.currentPlayer < 0) state.currentPlayer = state.playerNames.size() - 1;
    }

    static String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) out += " ";
        }
        return out;
    }

    static void selfTest() {
        int passed = 0;
        if (color("R5").equals("R")) passed++; else fail("color R5");
        if (rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        if (points("W4") == 50) passed++; else fail("wild points");
        if (isLegal("R2", "R9", "")) passed++; else fail("same color");
        if (isLegal("G9", "R9", "")) passed++; else fail("same number");
        if (isLegal("B3", "W", "B")) passed++; else fail("called color");
        if (!isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");

        ArrayList<String> h = new ArrayList<>();
        h.add("B3");
        h.add("R4");
        h.add("W");
        state.upCard = "R9";
        state.calledColor = "";
        if (chooseBotCard(h) == 1) passed++; else fail("bot normal before wild");

        ArrayList<String> h2 = new ArrayList<>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");
        if (chooseBotColor(h2).equals("B")) passed++; else fail("bot color");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
