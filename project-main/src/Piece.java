import java.awt.Color;

public class Piece {
    private Color color;
    private PlayerType playerType;


    public Piece(Color color,PlayerType playerType) {
        this.playerType = playerType;
        this.color = color;
    }
    public Piece(Piece other) {
        this.color = other.color;
        this.playerType = other.playerType;
    }


    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }


    public Color getColor() {
        return color;
    }
}
