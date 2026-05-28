import java.util.ArrayList;

// holds all mutable game state for one session
// pulling this out of Main removes the global-state smell and means
// a future version could run isolated games without resetting static fields
class GameState {
    ArrayList<String> playerNames = new ArrayList<>();
    ArrayList<Boolean> humanPlayers = new ArrayList<>();
    ArrayList<ArrayList<String>> hands = new ArrayList<>();
    ArrayList<String> deck = new ArrayList<>();
    ArrayList<String> discard = new ArrayList<>();
    int[] scores = new int[10];
    int currentPlayer = 0;
    int direction = 1;
    String upCard = "";
    String calledColor = "";
}

