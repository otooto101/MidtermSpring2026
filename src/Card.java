// Value object for a single UNO card.
// All card knowledge (color, rank, legality) lives here instead of scattered
// across Main as static string-parsing methods.
public class Card {

    private final String code;

    public Card(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public String color() {
        if (code.startsWith("R")) return "R";
        if (code.startsWith("Y")) return "Y";
        if (code.startsWith("G")) return "G";
        if (code.startsWith("B")) return "B";
        return "";
    }

    public String rank() {
        if (code.equals("W"))  return "WILD";
        if (code.equals("W4")) return "WILD_DRAW_FOUR";
        if (code.endsWith("S"))  return "SKIP";
        if (code.endsWith("R"))  return "REVERSE";
        if (code.endsWith("+2")) return "DRAW_TWO";
        return "NUMBER";
    }

    public int number() {
        if (rank().equals("NUMBER")) {
            return Integer.parseInt(code.substring(1));
        }
        return -1;
    }

    public int points() {
        String r = rank();
        if (r.equals("NUMBER")) return number();
        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) return 20;
        if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) return 50;
        return 0;
    }

    public boolean isWild() {
        return code.startsWith("W");
    }

    // checks if this card can legally be played on top of upCode given the called color
    public boolean isLegalOn(String upCode, String calledColor) {
        if (isWild()) return true;
        Card up = new Card(upCode);
        if (color().equals(up.color())) return true;
        if (!calledColor.equals("") && color().equals(calledColor)) return true;
        if (rank().equals(up.rank()) && !rank().equals("NUMBER")) return true;
        if (rank().equals("NUMBER") && up.rank().equals("NUMBER") && number() == up.number()) return true;
        return false;
    }

    @Override
    public String toString() {
        return code;
    }
}

