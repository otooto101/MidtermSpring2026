import jakarta.persistence.*;

// JPA entity: one player's total score in a session
@Entity
@Table(name = "player_score")
@Access(AccessType.FIELD)
public class PlayerScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private GameSessionEntity session;

    private String playerName;
    private int totalScore;
    private boolean winner;

    public Long getId()                        { return id; }
    public GameSessionEntity getSession()      { return session; }
    public void setSession(GameSessionEntity s){ session = s; }
    public String getPlayerName()              { return playerName; }
    public void setPlayerName(String v)        { playerName = v; }
    public int getTotalScore()                 { return totalScore; }
    public void setTotalScore(int v)           { totalScore = v; }
    public boolean isWinner()                  { return winner; }
    public void setWinner(boolean v)           { winner = v; }
}

