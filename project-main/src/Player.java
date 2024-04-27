import java.awt.*;

public class Player {
    private String name;
    private int numOfPieces;
    private PlayerType playerType;
    private Color color;
    private int phase; // 0: placement, 1: flying

    public Player(String name, PlayerType playerType,Color color) {
        this.name = name;
        this.numOfPieces = 0;
        this.playerType = playerType;
        this.color = color;
        this.phase = 0;
    }
    public Player(Player other) {
        this.name = other.name;
        this.numOfPieces = other.numOfPieces;
        this.playerType = other.playerType;
        this.color = other.color;
        this.phase = other.phase;
    }


    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfPieces() {
        return numOfPieces;
    }

    public void setNumOfPieces(int numOfPieces) {
        this.numOfPieces = numOfPieces;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public void increasePieceCount() {
        numOfPieces++;
    }

    public void decreasePieceCount() {
        numOfPieces--;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPhase() {return phase;
    }
}


