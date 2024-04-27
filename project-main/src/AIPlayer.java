import java.awt.*;
import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {
    private Random random;

    public AIPlayer(String name,  Color color) {
        super(name, PlayerType.AI, color);
        this.random = new Random();
    }

    public Move getRandomMove(Board board, int gamePhase) {
        System.out.println("Getting random move for AIPlayer. Game phase: " + gamePhase);
        switch (gamePhase) {
            case 0: // Placement phase
                List<Position> emptyPositions = board.getEmptyPositions();
                Position targetPosition;
                // We select a random position from the empty positions and
                // verify that no piece is already present at this position.
                do {
                    targetPosition = emptyPositions.get(random.nextInt(emptyPositions.size()));
                } while (board.getPiece(targetPosition) != null);
                return new Move(null, targetPosition);

            case 1: // Move or Flying phase
                List<Position> playerPieces = board.getPieces(getPlayerType());
                System.out.println("Player pieces: " + playerPieces);
                Position startPosition;
                List<Position> validMoves;
                do {
                    startPosition = playerPieces.get(random.nextInt(playerPieces.size()));
                    // If player has less than 4 pieces, he can fly. Hence we get all empty positions as valid moves.
                    // Otherwise, we only get the positions next to the startPosition as valid moves.
                    validMoves = playerPieces.size() <= 3 ? board.getEmptyPositions() : board.getValidMoves(startPosition);
                } while (validMoves.isEmpty() || board.getPiece(validMoves.get(0)) != null);
                // We select a random position from the valid moves as the target position.
                targetPosition = validMoves.get(random.nextInt(validMoves.size()));
                return new Move(startPosition, targetPosition);
        }
        return null; // Should never happen
    }

    public Position getRandomRemovePiece(Board board) {
        System.out.println("Getting random piece to remove for AIPlayer");
        List<Position> opponentPieces = board.getOpponentNonMillPieces(getPlayerType() == PlayerType.PLAYER_1 ? PlayerType.PLAYER_2 : PlayerType.PLAYER_1);
        // We select a random piece of the opponent that is not part of a mill to be removed.
        if (!opponentPieces.isEmpty()) {
            Position position = opponentPieces.get(random.nextInt(opponentPieces.size()));
            System.out.println("Selected piece to remove: " + position);
            return position;
        } else {
            System.out.println("No valid pieces to remove");
            return null;
        }
    }
}



