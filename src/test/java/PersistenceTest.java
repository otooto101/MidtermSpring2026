import java.time.LocalDateTime;
import java.util.List;

// persistence layer tests — run against an isolated in-memory H2 database (uno-test-pu)
// no manual setup required: schema is created and dropped automatically
public class PersistenceTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        // suppress Hibernate verbose startup messages
        java.util.logging.Logger.getLogger("org.hibernate")
                .setLevel(java.util.logging.Level.WARNING);

        System.out.println("=== Persistence Tests ===\n");

        // all tests share one EMF so create-drop doesn't wipe data between cases
        PersistenceService.init("uno-test-pu");
        try {
            testSaveAndRecentGames();
            testPlayerWinCounts();
            testHighestScores();
            testMultipleSessions();
        } finally {
            PersistenceService.close();
        }

        System.out.printf("%n==============================%n");
        System.out.printf("TOTAL: %d passed, %d failed%n", passed, failed);
        System.out.println("==============================");

        if (failed > 0) System.exit(1);
    }

    // ---- helpers to build entities without needing a real game ----

    static GameSessionEntity buildSession(String winner, int rounds,
                                          String[] players, int[] scores) {
        GameSessionEntity s = new GameSessionEntity();
        s.setStartedAt(LocalDateTime.now());
        s.setFinishedAt(LocalDateTime.now());
        s.setWinnerName(winner);
        s.setTotalRounds(rounds);

        for (int i = 0; i < rounds; i++) {
            RoundEntity r = new RoundEntity();
            r.setSession(s);
            r.setRoundNumber(i + 1);
            r.setWinnerName(winner);
            r.setPointsScored(scores[0]);
            s.getRounds().add(r);
        }

        for (int i = 0; i < players.length; i++) {
            PlayerScoreEntity ps = new PlayerScoreEntity();
            ps.setSession(s);
            ps.setPlayerName(players[i]);
            ps.setTotalScore(scores[i]);
            ps.setWinner(players[i].equals(winner));
            s.getPlayerScores().add(ps);
        }
        return s;
    }

    static void testSaveAndRecentGames() {
        System.out.println("  [save and recentGames query]");

        GameSessionEntity s = buildSession("Bot1", 2,
                new String[]{"Bot1", "Bot2"}, new int[]{25, 10});
        PersistenceService.saveSession(s);

        List<GameSessionEntity> recent = PersistenceService.recentGames(10);
        check("at least one session saved", recent.size() >= 1);
        check("winner name persisted correctly", recent.get(0).getWinnerName().equals("Bot1"));
        check("total rounds persisted correctly", recent.get(0).getTotalRounds() == 2);

        List<GameSessionEntity> limited = PersistenceService.recentGames(1);
        check("recentGames limit is respected", limited.size() <= 1);

        System.out.println();
    }

    static void testPlayerWinCounts() {
        System.out.println("  [playerWinCounts query]");

        // Bot1 wins again — now has 2 total wins across sessions
        GameSessionEntity s = buildSession("Bot1", 1,
                new String[]{"Bot1", "Bot2"}, new int[]{15, 5});
        PersistenceService.saveSession(s);

        List<Object[]> counts = PersistenceService.playerWinCounts();
        check("win counts list is not empty", !counts.isEmpty());
        check("Bot1 appears in win counts",
                counts.stream().anyMatch(r -> "Bot1".equals(r[0])));
        // Bot1 should be first (most wins)
        check("Bot1 is the top winner", "Bot1".equals(counts.get(0)[0]));

        System.out.println();
    }

    static void testHighestScores() {
        System.out.println("  [highestScores query]");

        // add a high-scorer session to make assertions deterministic
        GameSessionEntity s = buildSession("Bot3", 3,
                new String[]{"Bot3", "Bot4"}, new int[]{100, 20});
        PersistenceService.saveSession(s);

        List<PlayerScoreEntity> top = PersistenceService.highestScores(5);
        check("highest scores list is not empty", !top.isEmpty());
        check("top score is 100", top.get(0).getTotalScore() == 100);
        check("top scorer is Bot3", "Bot3".equals(top.get(0).getPlayerName()));
        check("session reference is accessible", top.get(0).getSession() != null);

        System.out.println();
    }

    static void testMultipleSessions() {
        System.out.println("  [multiple sessions ordering]");

        GameSessionEntity older = buildSession("BotX", 1,
                new String[]{"BotX"}, new int[]{5});
        older.setStartedAt(LocalDateTime.now().minusDays(1));
        PersistenceService.saveSession(older);

        GameSessionEntity newer = buildSession("BotY", 1,
                new String[]{"BotY"}, new int[]{5});
        newer.setStartedAt(LocalDateTime.now());
        PersistenceService.saveSession(newer);

        List<GameSessionEntity> recent = PersistenceService.recentGames(2);
        check("most recent session is listed first",
                recent.get(0).getStartedAt().isAfter(recent.get(1).getStartedAt())
                        || recent.get(0).getStartedAt().isEqual(recent.get(1).getStartedAt()));

        System.out.println();
    }

    // ---- framework ----

    static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("    PASS  " + description);
            passed++;
        } else {
            System.out.println("    FAIL  " + description);
            failed++;
        }
    }
}



