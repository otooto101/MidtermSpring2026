import java.util.ArrayList;
import java.util.Random;

// holds all mutable game state for one session
class GameState {
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<String> deck = new ArrayList<>();
    ArrayList<String> discard = new ArrayList<>();
    int[] scores = new int[10];
    int currentPlayer = 0;
    int direction = 1;
    String upCard = "";
    String calledColor = "";
    Random random = new Random();
}

