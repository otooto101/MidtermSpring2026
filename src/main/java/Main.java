import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static GameState state = new GameState();
    static boolean quiet = false;
    static Scanner scanner = new Scanner(System.in);

    // persistence tracking — set per session/round
    static GameSessionEntity currentSession = null;
    static int currentRoundNumber = 0;

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean showStats = false;
        int targetScore = 0; // 0 = disabled; use --games count instead
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--target") && i + 1 < args.length) {
                targetScore = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--stats")) {
                showStats = true;
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--target N] [--human] [--quiet] [--seed N] [--stats]");
                return;
            }
        }

        // suppress verbose Hibernate startup noise so the CLI stays readable
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.WARNING);
        PersistenceService.init("uno-pu");

        if (showStats) {
            PersistenceService.printStats();
            PersistenceService.close();
            return;
        }

        state.random = new Random(seed);
        setupPlayers(bots, human);

        if (state.players.size() < 2 || state.players.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            PersistenceService.close();
            return;
        }

        currentSession = PersistenceService.startSession(state.players);

        if (targetScore > 0) {
            // multi-round game: keep playing rounds until someone reaches the target score
            int g = 0;
            int safety = 0;
            while (!hasReachedTarget(targetScore) && safety < 10000) {
                safety++;
                g++;
                currentRoundNumber = g;
                Display.gameStart(g);
                GameLog.gameStart(g, state.players.size());
                playGame();
            }
            Display.targetReached(targetScore);
            Display.finalScores(state.players, state.scores);
            Display.finalWinner(finalWinnerName());
        } else {
            for (int g = 1; g <= games; g++) {
                currentRoundNumber = g;
                Display.gameStart(g);
                GameLog.gameStart(g, state.players.size());
                playGame();
            }
            Display.finalScores(state.players, state.scores);
        }

        PersistenceService.finishSession(currentSession, state.players, state.scores);
        GameLog.gameEnd();
        PersistenceService.close();
    }

    // true when at least one player has reached (or passed) the target score
    static boolean hasReachedTarget(int target) {
        for (int s : state.scores) {
            if (s >= target) return true;
        }
        return false;
    }

    // player with the highest score; ties resolve to the first player found with that score
    static String finalWinnerName() {
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < state.players.size(); i++) {
            if (state.scores[i] > bestScore) {
                bestScore = state.scores[i];
                bestIndex = i;
            }
        }
        return bestIndex >= 0 ? state.players.get(bestIndex).name : "";
    }

    static void setupPlayers(int bots, boolean human) {
        state.players.clear();
        if (human) {
            state.players.add(new Player("You", true));
        }
        for (int i = 1; i <= bots; i++) {
            state.players.add(new Player("Bot" + i, false));
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
        state.currentPlayer = state.random.nextInt(state.players.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            Player current = state.players.get(state.currentPlayer);
            checkUnoPenalty(current);

            Display.turnState(state.upCard, state.calledColor, current.name, current.hand);
            GameLog.playerTurn(current.name, state.upCard, state.calledColor);

            int chosen = -1;
            if (current.isHuman) {
                chosen = askHuman(current.hand);
            } else {
                chosen = chooseBotCard(current.hand);
            }

            if (chosen == -1) {
                chosen = handleDraw(current.hand, current.name);
            }

            if (chosen >= 0) {
                if (chosen >= current.hand.size()) {
                    Display.penaltyCard(current.name);
                    current.hand.add(draw());
                    next();
                    continue;
                }

                String card = current.hand.get(chosen);

                if (!isLegal(card, state.upCard, state.calledColor)) {
                    Display.illegalCard(current.name, card);
                    current.hand.add(draw());
                    next();
                    continue;
                }

                current.hand.remove(chosen);
                state.discard.add(state.upCard);
                state.upCard = card;
                state.calledColor = "";
                Display.playerPlays(current.name, card);
                GameLog.cardPlayed(current.name, card);

                if (card.equals("W") || card.equals("W4")) {
                    if (current.isHuman) {
                        state.calledColor = askColor();
                    } else {
                        state.calledColor = chooseBotColor(current.hand);
                    }
                    Display.playerCallsColor(current.name, state.calledColor);
                }

                if (current.hand.size() == 1) {
                    Display.uno(current.name);
                    if (current.isHuman) {
                        current.saidUno = askUnoCall();
                    } else {
                        current.saidUno = true; // bots always call it (documented simplification)
                    }
                }

                if (current.hand.size() == 0) {
                    scoreRound(current.name);
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
        GameLog.cardDrawn(name, drawn);
        if (isLegal(drawn, state.upCard, state.calledColor)) {
            if (!state.players.get(state.currentPlayer).isHuman) {
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
        for (int i = 0; i < state.players.size(); i++) {
            state.players.get(i).hand.clear();
            state.players.get(i).saidUno = false;
            for (int j = 0; j < 7; j++) {
                state.players.get(i).hand.add(draw());
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

    // checks if the player is sitting at 1 card without having called UNO,
    // and applies the missed-call penalty (draw 2) at the start of their turn
    static void checkUnoPenalty(Player p) {
        if (p.hand.size() == 1 && !p.saidUno) {
            p.hand.add(draw());
            p.hand.add(draw());
            Display.missedUnoPenalty(p.name);
            GameLog.unoPenalty(p.name);
            p.saidUno = false;
        }
    }

    static boolean askUnoCall() {
        System.out.print("Call UNO? y/n: ");
        String input = scanner.nextLine().trim();
        return input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("uno");
    }

    static void scoreRound(String winnerName) {
        int points = 0;
        for (int i = 0; i < state.players.size(); i++) {
            if (i != state.currentPlayer) {
                for (int j = 0; j < state.players.get(i).hand.size(); j++) {
                    points += points(state.players.get(i).hand.get(j));
                }
            }
        }
        state.scores[state.currentPlayer] += points;
        Display.winsRound(winnerName, points);
        GameLog.roundEnd(winnerName, points);
        if (currentSession != null) {
            PersistenceService.addRound(currentSession, currentRoundNumber, winnerName, points);
        }
    }

    static void applyCardEffect(String card) {
        switch (rank(card)) {
            case "SKIP":
                next();
                next();
                break;
            case "REVERSE":
                state.direction = state.direction * -1;
                if (state.players.size() == 2) {
                    next();
                    next();
                } else {
                    next();
                }
                break;
            case "DRAW_TWO":
                next();
                state.players.get(state.currentPlayer).hand.add(draw());
                state.players.get(state.currentPlayer).hand.add(draw());
                Display.drawsTwo(state.players.get(state.currentPlayer).name);
                next();
                break;
            case "WILD_DRAW_FOUR":
                next();
                for (int i = 0; i < 4; i++) {
                    state.players.get(state.currentPlayer).hand.add(draw());
                }
                Display.drawsFour(state.players.get(state.currentPlayer).name);
                next();
                break;
            default:
                next();
                break;
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
                GameLog.invalidInput("You", "card-not-found:" + input);
                continue;
            }
            if (!isLegal(hand.get(idx), state.upCard, state.calledColor)) {
                System.out.println("That card is not legal.");
                GameLog.invalidInput("You", "illegal-card:" + hand.get(idx));
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

    // quick helpers so i don't have to write new Card(...) everywhere
    static boolean isLegal(String card, String up, String call) { return new Card(card).isLegalOn(up, call); }
    static String color(String card)  { return new Card(card).color(); }
    static String rank(String card)   { return new Card(card).rank(); }
    static int    number(String card) { return new Card(card).number(); }
    static int    points(String card) { return new Card(card).points(); }

    static void next() {
        state.currentPlayer += state.direction;
        if (state.currentPlayer >= state.players.size()) state.currentPlayer = 0;
        if (state.currentPlayer < 0) state.currentPlayer = state.players.size() - 1;
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
        h.add("B3"); h.add("R4"); h.add("W");
        state.upCard = "R9";
        state.calledColor = "";
        if (chooseBotCard(h) == 1) passed++; else fail("bot normal before wild");

        ArrayList<String> h2 = new ArrayList<>();
        h2.add("B1"); h2.add("B2"); h2.add("R3");
        if (chooseBotColor(h2).equals("B")) passed++; else fail("bot color");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
