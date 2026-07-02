import java.util.ArrayList;

// all the print statements live here so the game logic isn't cluttered with sysouts
public class Display {

    static void gameStart(int gameNumber) {
        if (!Main.quiet) System.out.println("\n=== Game " + gameNumber + " ===");
    }

    static void turnState(String upCard, String calledColor, String playerName, ArrayList<String> hand) {
        if (!Main.quiet) {
            System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
            System.out.println(playerName + " hand: " + Main.join(hand));
        }
    }

    static void playerDraws(String playerName, String card) {
        if (!Main.quiet) System.out.println(playerName + " draws " + card);
    }

    static void penaltyCard(String playerName) {
        if (!Main.quiet) System.out.println(playerName + " selected an invalid index and draws a penalty card.");
    }

    static void illegalCard(String playerName, String card) {
        if (!Main.quiet) System.out.println(playerName + " tried illegal card " + card + " and draws a penalty card.");
    }

    static void playerPlays(String playerName, String card) {
        if (!Main.quiet) System.out.println(playerName + " plays " + card);
    }

    static void playerCallsColor(String playerName, String color) {
        if (!Main.quiet) System.out.println(playerName + " calls " + color);
    }

    static void uno(String playerName) {
        if (!Main.quiet) System.out.println(playerName + " says UNO!");
    }

    static void winsRound(String playerName, int points) {
        if (!Main.quiet) System.out.println(playerName + " wins and scores " + points);
    }

    static void drawsTwo(String playerName) {
        if (!Main.quiet) System.out.println(playerName + " draws two.");
    }

    static void drawsFour(String playerName) {
        if (!Main.quiet) System.out.println(playerName + " draws four.");
    }

    static void missedUnoPenalty(String playerName) {
        if (!Main.quiet) System.out.println(playerName + " forgot to call UNO and draws two penalty cards.");
    }

    static void targetReached(int target) {
        if (!Main.quiet) System.out.println("\nTarget score of " + target + " reached!");
    }

    static void finalWinner(String playerName) {
        System.out.println("Overall winner: " + playerName);
    }

    static void safetyLimit() {
        if (!Main.quiet) System.out.println("Game stopped at safety limit.");
    }

    static void finalScores(ArrayList<Player> players, int[] scores) {
        System.out.println("\nFinal scores:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).name + ": " + scores[i]);
        }
    }
}

