import jakarta.persistence.*;

// JPA entity: one round result within a session
@Entity
@Table(name = "round")
@Access(AccessType.FIELD)
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private GameSessionEntity session;

    private int roundNumber;
    private String winnerName;
    private int pointsScored;

    public Long getId()                       { return id; }
    public GameSessionEntity getSession()     { return session; }
    public void setSession(GameSessionEntity s) { session = s; }
    public int getRoundNumber()               { return roundNumber; }
    public void setRoundNumber(int v)         { roundNumber = v; }
    public String getWinnerName()             { return winnerName; }
    public void setWinnerName(String v)       { winnerName = v; }
    public int getPointsScored()              { return pointsScored; }
    public void setPointsScored(int v)        { pointsScored = v; }
}

