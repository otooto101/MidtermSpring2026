import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// JPA entity: one complete session (all rounds in a single run of the program)
@Entity
@Table(name = "game_session")
@Access(AccessType.FIELD)
public class GameSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String winnerName;
    private int totalRounds;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoundEntity> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerScoreEntity> playerScores = new ArrayList<>();

    public Long getId()                              { return id; }
    public LocalDateTime getStartedAt()              { return startedAt; }
    public void setStartedAt(LocalDateTime v)        { startedAt = v; }
    public LocalDateTime getFinishedAt()             { return finishedAt; }
    public void setFinishedAt(LocalDateTime v)       { finishedAt = v; }
    public String getWinnerName()                    { return winnerName; }
    public void setWinnerName(String v)              { winnerName = v; }
    public int getTotalRounds()                      { return totalRounds; }
    public void setTotalRounds(int v)                { totalRounds = v; }
    public List<RoundEntity> getRounds()             { return rounds; }
    public List<PlayerScoreEntity> getPlayerScores() { return playerScores; }
}

