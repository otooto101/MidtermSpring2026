import java.util.ArrayList;

// holds everything about one player: name, human or bot, and their cards
class Player {
    String name;
    boolean isHuman;
    ArrayList<String> hand = new ArrayList<>();

    Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }
}

