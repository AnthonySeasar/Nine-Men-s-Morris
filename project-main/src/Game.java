import java.awt.*;
import java.util.LinkedList;
import java.util.List;


public class Game {
    private Player player1;
    private Player player2;
    private Board board;
    private boolean isPlaying;
    private Player currentPlayer;
    private int turn;
    private int phase; // 0: placement, 1: moving, 2: flying
    private int[] piecesToPlace; // pieces left to be placed for each player
    private int[] piecesLeft; // total pieces left for each player
    private boolean removeMode; // true if the player has to remove a piece of the opponent
    private AIPlayer aiPlayer;
    public boolean isPlayingWithAI;
    private Player lastPlayerMadeMove = null;


    public Game(String player1Name, String player2Name, boolean playWithAI) {
        this.player1 = new Player(player1Name, PlayerType.PLAYER_1, Color.darkGray);
        this.isPlayingWithAI = playWithAI;
        if (playWithAI) {
            // 其中一个玩家是AI
            this.player2 = new AIPlayer(player2Name,Color.lightGray);
        } else {
            // 两个玩家都是人类玩家
            this.player2 = new Player(player2Name, PlayerType.PLAYER_2,Color.lightGray);
        }
//        this.player1 = new Player(player1Name, PlayerType.PLAYER_1, Color.darkGray);
//        this.player2 = new Player(player2Name, PlayerType.PLAYER_2,Color.lightGray);
        board = new Board();
        isPlaying = false;
        currentPlayer = player1;
        phase = 0;
        piecesToPlace = new int[] {9, 9};
        piecesLeft = new int[] {9, 9};
        removeMode = false;
        initializeBoard( player1,player2);
    }

    public Board getBoard() {
        return this.board;
    }
    public void initializeBoard(Player player1, Player player2) {
        // 现在这个方法只是简单地创建了一个空棋盘，没有为任何玩家放置任何棋子
    }
    public void start() {
        isPlaying = true;
        //board.initializeBoard(player1, player2);
        board = new Board();
        turn = 0;
        phase = 0;
        removeMode = false;
        //nextPlayer(); //  players take turns to place their pieces
    }

    public boolean makeMove() {
        boolean successfulMove = false;

        // If it's AIPlayer's turn and we are playing with AI, let AIPlayer make a move
        if (!removeMode && isPlayingWithAI && currentPlayer.getPlayerType() == PlayerType.AI) {
            System.out.println("Making move for AIPlayer");

            // Add a check and cast currentPlayer to AIPlayer
            if (currentPlayer instanceof AIPlayer) {
                AIPlayer aiPlayer = (AIPlayer) currentPlayer;
                Move move = aiPlayer.getRandomMove(board, getPhase());
                System.out.println("AIPlayer generated move: " + move);
                successfulMove = applyMove(move);
                if (successfulMove) {
                    System.out.println("AIPlayer made a move: " + move);
                    // check if a mill is formed after successful move
                    if (board.formMill(move.getTargetPosition())) {
                        removeMode = true; // enter remove mode
                        Position removePos = aiPlayer.getRandomRemovePiece(board);
                        applyRemovePiece(removePos); // let AI remove a piece
                        // Don't allow AI to continue playing in remove mode
                        return true;
                    }
                } else {
                    System.out.println("AIPlayer failed to make a move");
                }
            }
        }

        return successfulMove;
    }





    public boolean applyMove(Move move) {
        boolean successfulMove = false;

        // Use the phase of the current game
        int currentPhase = getPhase();

        if (currentPhase == 0) {
            // If in placement phase
            if (piecesToPlace[currentPlayer.getPlayerType() == PlayerType.PLAYER_1 ? 0 : 1] <= 0) {
                return false; // The current player has no pieces left to place
            }
            if (board.getPiece(move.getTargetPosition()) == null) {
                // The target position is unoccupied
                board.setPiece(move.getTargetPosition(), new Piece(currentPlayer.getColor(), currentPlayer.getPlayerType()));
                successfulMove = true;
                decrementPiecesToPlace(currentPlayer.getPlayerType());
                // 如果所有的棋子都已经被放置，那么更新游戏阶段为移动阶段
                if (piecesToPlace[0] == 0 && piecesToPlace[1] == 0) {
                    setPhase(1);
                }
            }
        } else {
            // If in moving or flying phase
            // Get the current player's phase
            int playerPhase = currentPlayer.getPhase();

            if (playerPhase == 0) {
                // If in normal movement phase
                if (board.isValidMove(move, false)) {
                    // The move is valid
                    Piece piece = board.getPiece(move.getStartPosition());
                    board.removePiece(move.getStartPosition());
                    board.setPiece(move.getTargetPosition(), piece);
                    successfulMove = true;
                }
            } else if (playerPhase == 1) {
                // If in flying phase
                if (board.getPiece(move.getTargetPosition()) == null) {
                    // The target position is unoccupied
                    Piece piece = board.getPiece(move.getStartPosition());
                    board.removePiece(move.getStartPosition());
                    board.setPiece(move.getTargetPosition(), piece);
                    successfulMove = true;
                }
            }
        }

        // Only increment turn if successful move was made
        if (successfulMove) {
            incrementTurn();
            updatePlayerPhase(currentPlayer.getPlayerType());
        }

        return successfulMove;
    }

    /*public boolean makeMove(Move move) {
        boolean successfulMove = false;

        // Use the phase of the current game
        int currentPhase = getPhase();

        if (currentPhase == 0) {
            // If in placement phase
            if (piecesToPlace[currentPlayer.getPlayerType() == PlayerType.PLAYER_1 ? 0 : 1] <= 0) {
                return false; // The current player has no pieces left to place
            }
            if (board.getPiece(move.getTargetPosition()) == null) {
                // The target position is unoccupied
                board.setPiece(move.getTargetPosition(), new Piece(currentPlayer.getColor(), currentPlayer.getPlayerType()));
                successfulMove = true;
                decrementPiecesToPlace(currentPlayer.getPlayerType());
                // 如果所有的棋子都已经被放置，那么更新游戏阶段为移动阶段
                if (piecesToPlace[0] == 0 && piecesToPlace[1] == 0) {
                    setPhase(1);
                }
            }
        } else {
            // If in moving or flying phase
            // Get the current player's phase
            int playerPhase = currentPlayer.getPhase();

            if (playerPhase == 0) {
                // If in normal movement phase
                if (board.isValidMove(move, false)) {
                    // The move is valid
                    Piece piece = board.getPiece(move.getStartPosition());
                    board.removePiece(move.getStartPosition());
                    board.setPiece(move.getTargetPosition(), piece);
                    successfulMove = true;
                }
            } else if (playerPhase == 1) {
                // If in flying phase
                if (board.getPiece(move.getTargetPosition()) == null) {
                    // The target position is unoccupied
                    Piece piece = board.getPiece(move.getStartPosition());
                    board.removePiece(move.getStartPosition());
                    board.setPiece(move.getTargetPosition(), piece);
                    successfulMove = true;
                }
            }
        }

        // Only increment turn if successful move was made
        if (successfulMove) {
            incrementTurn();
            updatePlayerPhase(currentPlayer.getPlayerType());
        }

        return successfulMove;
    }
*/


    public void nextPlayer() {
        System.out.println("Player1: " + player1 + ", Player2: " + player2 + ", CurrentPlayer: " + currentPlayer);

        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
        System.out.println("After switching, CurrentPlayer: " + currentPlayer);

    }
    public Player getNextPlayer() {
        if (currentPlayer == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void incrementTurn() {
        turn++;
    }
    public boolean isPlaying() {
        return isPlaying;
    }


    public int getTurn() {
        return turn;
    }
    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean inRemoveMode() {
        return removeMode;
    }

    public void setRemoveMode(boolean removeMode) {
        this.removeMode = removeMode;
    }

    public boolean isGameOver() {
        // Game is over if one player has only two pieces left or cannot move
        if (board.getNumOfPiecesP1() <= 2 || !canMove(player1)) {
            return true;
        }
        if (board.getNumOfPiecesP2() <= 2 || !canMove(player2)) {
            return true;
        }
        return false;
    }

    public Player getWinner() {
        // The winner is the player who is not game over
        if (board.getNumOfPiecesP1() > 2 && canMove(player1)) {
            return player1;
        }
        if (board.getNumOfPiecesP2() > 2 && canMove(player2)) {
            return player2;
        }
        return null;
    }

    private boolean canMove(Player player) {
        // A player can move if they have at least one piece that can move to an empty position
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Position position = new Position(x, y);
                Piece piece = board.getPiece(position);
                if (piece != null && piece.getPlayerType() == player.getPlayerType()) {
                    if ((player.getPhase() == 0 && hasEmptyAdjacent(position)) ||
                            (player.getPhase() == 1 && hasEmptySpot())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean hasEmptyAdjacent(Position position) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions) {
            int x = position.getX() + direction[0];
            int y = position.getY() + direction[1];
            Position adjacentPosition = new Position(x, y);
            if (board.isValidPosition(adjacentPosition) && board.getPiece(adjacentPosition) == null) {
                return true;
            }
        }
        return false;
    }
    private boolean hasEmptySpot() {
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Position position = new Position(x, y);
                if (board.isValidPosition(position) && board.getPiece(position) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updatePhase() {
        if (phase == 0 && piecesToPlace[0] == 0 && piecesToPlace[1] == 0) {
            phase = 1; // All pieces have been placed, move to the moving phase
        } else if (phase == 1 && (piecesLeft[0] <= 3 || piecesLeft[1] <= 3)) {
            phase = 2; // One player has only 3 pieces left, move to the flying phase
        }
    }

    public int getPiecesToPlace(PlayerType playerType) {
        int index = playerType == PlayerType.PLAYER_1 ? 0 : 1;
        return piecesToPlace[index];
    }


    public int getPiecesLeft(PlayerType playerType) {
        int index = playerType == PlayerType.PLAYER_1 ? 0 : 1;
        return piecesLeft[index];
    }

    public void decrementPiecesToPlace(PlayerType playerType) {
        int index = playerType == PlayerType.PLAYER_1 ? 0 : 1;
        piecesToPlace[index]--;
        updatePlayerPhase(playerType); // Update the player phase after a piece has been placed
    }

    public void decrementPiecesLeft(PlayerType playerType) {
        int index = playerType == PlayerType.PLAYER_1 ? 0 : 1;
        piecesLeft[index]--;
        updatePlayerPhase(playerType); // Update the player phase after a piece has been placed
    }

    /*public void removePiece(Position position) {
        if (!removeMode) {
            return; // It's not the current player's turn to remove a piece
        }
        Piece piece = board.getPiece(position);
        if (piece == null || piece.getPlayerType() == currentPlayer.getPlayerType()) {
            return; // The position is empty or the piece belongs to the current player
        }
        board.removePiece(position);
        decrementPiecesLeft(piece.getPlayerType());
        updatePlayerPhase(piece.getPlayerType()); // 更新被移除棋子的玩家的阶段
        removeMode = false;
        nextPlayer();
    }*/
    public boolean isValidRemovePiece(Position position) {
        Piece piece = board.getPiece(position);
        PlayerType opponentType = (currentPlayer.getPlayerType() == PlayerType.PLAYER_1) ? PlayerType.PLAYER_2 : PlayerType.PLAYER_1;

        // if piece is in a mill, check if there are other pieces not in mills
        if (board.isInMill(position)) {
            List<Position> nonMillPieces = board.getOpponentNonMillPieces(opponentType);
            if (!nonMillPieces.isEmpty()) {
                // there are other pieces not in a mill, so this piece is not a valid choice
                return false;
            }
        }

        // the piece is either not in a mill, or all opponent's pieces are in mills
        return true;
    }

    private void updatePlayerPhase(PlayerType playerType) {
        int pieces = getPiecesLeft(playerType);
        int piecesToPlace = getPiecesToPlace(playerType);
        if (piecesToPlace > 0) {
            // If there are still pieces to place, the player is in the placement phase
            return;
        }
        if (playerType == PlayerType.PLAYER_1) {
            if (pieces > 3) {
                player1.setPhase(0); // Moving phase
            } else if (pieces <= 3) {
                player1.setPhase(1); // Flying phase
            }
        } else {
            if (pieces > 3) {
                player2.setPhase(0); // Moving phase
            } else if (pieces <= 3) {
                player2.setPhase(1); // Flying phase
            }
        }
    }

    public void removePiece() {
        if (!removeMode) {
            return; // It's not the current player's turn to remove a piece
        }

        // If it's AIPlayer's turn and we are playing with AI, let AIPlayer select a piece to remove
        if (isPlayingWithAI && currentPlayer.getPlayerType() == PlayerType.AI) {
            // Add a check and cast currentPlayer to AIPlayer
            if (currentPlayer instanceof AIPlayer) {
                AIPlayer aiPlayer = (AIPlayer) currentPlayer;
                Position position = aiPlayer.getRandomRemovePiece(board);
                applyRemovePiece(position);
            }
        }
    }

    public void applyRemovePiece(Position position) {
        if (position == null || !removeMode) {
            // If there is no valid position to remove or it's not the current player's turn to remove a piece
            return;
        }
        Piece piece = board.getPiece(position);
        if (piece == null || piece.getPlayerType() == currentPlayer.getPlayerType()) {
            return; // The position is empty or the piece belongs to the current player
        }
        board.removePiece(position);
        decrementPiecesLeft(piece.getPlayerType());
        updatePlayerPhase(piece.getPlayerType()); // 更新被移除棋子的玩家的阶段
        removeMode = false;
        nextPlayer();
    }



}




