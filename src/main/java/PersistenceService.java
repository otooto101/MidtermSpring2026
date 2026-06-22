import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// thin service layer so Main never touches EntityManager directly
public final class PersistenceService {

    private static EntityManagerFactory emf;
    private static GameRepository repo;

    private PersistenceService() {}

    public static void init(String persistenceUnitName) {
        emf  = Persistence.createEntityManagerFactory(persistenceUnitName);
        repo = new GameRepository(emf);
    }

    public static void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    // build a new in-memory session (not yet saved to DB)
    public static GameSessionEntity startSession(List<Player> players) {
        GameSessionEntity session = new GameSessionEntity();
        session.setStartedAt(LocalDateTime.now());
        for (Player p : players) {
            PlayerScoreEntity ps = new PlayerScoreEntity();
            ps.setPlayerName(p.name);
            ps.setSession(session);
            session.getPlayerScores().add(ps);
        }
        return session;
    }

    // record a completed round (accumulates in memory until finishSession saves it)
    public static void addRound(GameSessionEntity session, int roundNumber,
                                String winner, int points) {
        RoundEntity r = new RoundEntity();
        r.setSession(session);
        r.setRoundNumber(roundNumber);
        r.setWinnerName(winner);
        r.setPointsScored(points);
        session.getRounds().add(r);
        session.setTotalRounds(roundNumber);
    }

    // finalize and persist the whole session in one transaction
    public static void finishSession(GameSessionEntity session,
                                     List<Player> players, int[] scores) {
        // find overall winner (highest accumulated score)
        int maxScore = -1;
        String winner = players.isEmpty() ? "none" : players.get(0).name;
        for (int i = 0; i < players.size(); i++) {
            if (scores[i] > maxScore) {
                maxScore  = scores[i];
                winner    = players.get(i).name;
            }
        }

        session.setFinishedAt(LocalDateTime.now());
        session.setWinnerName(winner);

        // fill in final per-player totals
        for (int i = 0; i < players.size(); i++) {
            for (PlayerScoreEntity ps : session.getPlayerScores()) {
                if (ps.getPlayerName().equals(players.get(i).name)) {
                    ps.setTotalScore(scores[i]);
                    ps.setWinner(ps.getPlayerName().equals(winner));
                }
            }
        }

        repo.save(session);
    }

    // used by tests to persist a manually-built session
    public static void saveSession(GameSessionEntity session) {
        repo.save(session);
    }

    // query methods exposed so tests don't need to create their own EMF
    public static java.util.List<GameSessionEntity> recentGames(int limit) {
        return repo.recentGames(limit);
    }

    public static java.util.List<Object[]> playerWinCounts() {
        return repo.playerWinCounts();
    }

    public static java.util.List<PlayerScoreEntity> highestScores(int limit) {
        return repo.highestScores(limit);
    }

    // print all three required query reports to stdout
    public static void printStats() {
        System.out.println("\n=== Recent Games (last 5) ===");
        List<GameSessionEntity> recent = repo.recentGames(5);
        if (recent.isEmpty()) {
            System.out.println("  No games recorded yet.");
        }
        for (GameSessionEntity g : recent) {
            System.out.printf("  [%s]  winner=%-10s  rounds=%d%n",
                    g.getStartedAt(), g.getWinnerName(), g.getTotalRounds());
        }

        System.out.println("\n=== Player Win Counts ===");
        List<Object[]> wins = repo.playerWinCounts();
        if (wins.isEmpty()) System.out.println("  No data.");
        for (Object[] row : wins) {
            System.out.printf("  %-12s  %s wins%n", row[0], row[1]);
        }

        System.out.println("\n=== Highest Session Scores (top 5) ===");
        List<PlayerScoreEntity> high = repo.highestScores(5);
        if (high.isEmpty()) System.out.println("  No data.");
        for (PlayerScoreEntity ps : high) {
            System.out.printf("  %-12s  %d pts  [%s]%n",
                    ps.getPlayerName(), ps.getTotalScore(),
                    ps.getSession().getStartedAt());
        }
    }
}


