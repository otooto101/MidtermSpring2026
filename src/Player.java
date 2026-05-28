import java.util.ArrayList;

// bundles name, type, and hand for one player
// replaces the three parallel arrays (playerNames, humanPlayers, hands)
class Player {
    String name;
    boolean isHuman;
    ArrayList<String> hand = new ArrayList<>();

    Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }
}

