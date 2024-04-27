public class Move {
    private Position startPosition;
    private Position targetPosition;
    private PlayerType playerType;

    public Move(Position startPosition, Position targetPosition, PlayerType playerType) {
        this.startPosition = startPosition;
        this.targetPosition = targetPosition;
        this.playerType = playerType;
    }
    public Move(Position startPosition, Position targetPosition) {
        this.startPosition = startPosition;
        this.targetPosition = targetPosition;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
