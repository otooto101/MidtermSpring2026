import jakarta.persistence.*;
import java.util.List;

// DAO: all database access goes through here — no SQL in game logic
public class GameRepository {

    private final EntityManagerFactory emf;

    public GameRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // persist a fully-built session (rounds and player scores included via cascade)
    public void save(GameSessionEntity session) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // query 1: most recent N sessions
    public List<GameSessionEntity> recentGames(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT g FROM GameSessionEntity g ORDER BY g.startedAt DESC",
                    GameSessionEntity.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // query 2: win count per player, descending
    public List<Object[]> playerWinCounts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p.playerName, COUNT(p) FROM PlayerScoreEntity p " +
                    "WHERE p.winner = true GROUP BY p.playerName ORDER BY COUNT(p) DESC",
                    Object[].class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // query 3: top N individual session scores
    public List<PlayerScoreEntity> highestScores(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PlayerScoreEntity p ORDER BY p.totalScore DESC",
                    PlayerScoreEntity.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

