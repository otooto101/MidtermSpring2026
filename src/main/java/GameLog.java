import java.util.logging.Logger;

// logs key game events via java.util.logging (stderr) so CLI stdout stays clean for players
public final class GameLog {

    private static final Logger LOG = Logger.getLogger("uno");

    private GameLog() {}

    public static void gameStart(int gameNumber, int players) {
        LOG.info(() -> "GAME_START game=" + gameNumber + " players=" + players);
    }

    public static void playerTurn(String name, String upCard, String calledColor) {
        LOG.info(() -> "PLAYER_TURN player=" + name + " up=" + upCard
                + (calledColor.isEmpty() ? "" : " called=" + calledColor));
    }

    public static void cardPlayed(String name, String card) {
        LOG.info(() -> "CARD_PLAYED player=" + name + " card=" + card);
    }

    public static void cardDrawn(String name, String card) {
        LOG.info(() -> "CARD_DRAWN player=" + name + " card=" + card);
    }

    public static void invalidInput(String name, String detail) {
        LOG.warning(() -> "INVALID_INPUT player=" + name + " detail=" + detail);
    }

    public static void roundEnd(String winner, int points) {
        LOG.info(() -> "ROUND_END winner=" + winner + " points=" + points);
    }

    public static void unoPenalty(String name) {
        LOG.warning(() -> "UNO_PENALTY player=" + name);
    }

    public static void gameEnd() {
        LOG.info("GAME_END");
    }
}

