import java.util.ArrayList;

// holds everything about one player: name, human or bot, and their cards
class Player {
    String name;
    boolean isHuman;
    ArrayList<String> hand = new ArrayList<>();
    // tracks whether this player has called "UNO" since last reaching 1 card
    boolean saidUno = false;

    Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }
}

