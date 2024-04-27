import java.util.*;
import java.util.stream.Collectors;

public class Board {
    private Piece[][] board;
    public static final int BOARD_SIZE = 7;
    private int numOfPiecesP1;
    private int numOfPiecesP2;
    private int numberOfTotalPiecesPlaced;
    private List<Mill> mills;  // 创建一个 Mill 列表来储存已形成的磨坊
    private Map<Position, List<Position>> adjacentPositions;//映射表明了每个位置的所有合法相邻位置。

    private static final int[][] VALID_POSITIONS = {
            {0, 0}, {0, 3}, {0, 6},
            {1, 1}, {1, 3}, {1, 5},
            {2, 2}, {2, 3}, {2, 4},
            {3, 0}, {3, 1}, {3, 2},
            {3, 4}, {3, 5}, {3, 6},
            {4, 2}, {4, 3}, {4, 4},
            {5, 1}, {5, 3}, {5, 5},
            {6, 0}, {6, 3}, {6, 6}
    };
    private int[][][] allPossibleMills = {// The array of all possible mills.
        {{0, 0}, {0, 3}, {0, 6}},
        {{1, 1}, {1, 3}, {1, 5}},
        {{2, 2}, {2, 3}, {2, 4}},
        {{0, 0}, {3, 0}, {6, 0}},
        {{1, 1}, {3, 1}, {5, 1}},
        {{2, 2}, {3, 2}, {4, 2}},
        {{3, 0}, {3, 1}, {3, 2}},
        {{3, 4}, {3, 5}, {3, 6}},
        {{4, 2}, {4, 3}, {4, 4}},
        {{5, 1}, {5, 3}, {5, 5}},
        {{6, 0}, {6, 3}, {6, 6}},
        {{0, 3}, {1, 3}, {2, 3}},
        {{4, 3}, {5, 3}, {6, 3}},
        {{2, 4}, {3, 4}, {4, 4}},
        {{1, 5}, {3, 5}, {5, 5}},
        {{0, 6}, {3, 6}, {6, 6}}
    };;

    public Board() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        numOfPiecesP1 = 0;
        numOfPiecesP2 = 0;
        numberOfTotalPiecesPlaced = 0;
        mills = new ArrayList<>();  // 初始化 Mill 列表
        adjacentPositions = new HashMap<>();
        initializeAdjacentPositions();

    }
    public Board(Board other) {
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (other.board[i][j] != null) {
                    this.board[i][j] = new Piece(other.board[i][j]);
                }
            }
        }

        this.numOfPiecesP1 = other.numOfPiecesP1;
        this.numOfPiecesP2 = other.numOfPiecesP2;
        this.numberOfTotalPiecesPlaced = other.numberOfTotalPiecesPlaced;

        // 复制磨坊列表中的元素
        this.mills = new ArrayList<>();
        for (Mill mill : other.mills) {
            this.mills.add(new Mill(mill));
        }

        // 复制每个相邻位置列表
        this.adjacentPositions = new HashMap<>();
        for (Map.Entry<Position, List<Position>> entry : other.adjacentPositions.entrySet()) {
            this.adjacentPositions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

/*    public void initializeBoard(Player player1, Player player2) {
        // 现在这个方法只是简单地创建了一个空棋盘，没有为任何玩家放置任何棋子
    }*/


    public int[][] getValidPositions() {
        return VALID_POSITIONS;
    }
    // 这个方法用于初始化相邻位置的映射
    private void initializeAdjacentPositions() {
        // 对于每一个合法位置，创建一个包含所有合法相邻位置的列表
        for (int[] pos : VALID_POSITIONS) {
            Position position = new Position(pos[0], pos[1]);
            List<Position> neighbors = new ArrayList<>();

            // 检查所有可能的相邻位置
            for (int[] offset : new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                Position neighbor = new Position(position.getX() + offset[0], position.getY() + offset[1]);

                // 如果相邻位置也是合法的，那么添加到列表中
                if (isValidPosition(neighbor)) {
                    neighbors.add(neighbor);
                }
            }

            adjacentPositions.put(position, neighbors);
        }
    }


    public boolean isValidPosition(Position position) {
        for (int[] validPosition : VALID_POSITIONS) {
            if (position.getX() == validPosition[0] && position.getY() == validPosition[1]) {
                return true;
            }
        }
        return false;
    }

    public Piece getPiece(Position position) {
        return board[position.getX()][position.getY()];
    }

    public void setPiece(Position position, Piece piece) {
        board[position.getX()][position.getY()] = piece;
        if (piece.getPlayerType() == PlayerType.PLAYER_1) {
            numOfPiecesP1++;
        } else {
            numOfPiecesP2++;
        }
        numberOfTotalPiecesPlaced++;
    }
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getStartPosition());
        if (piece != null && isValidMove(move, false)) {
            removePiece(move.getStartPosition());
            setPiece(move.getTargetPosition(), piece);
        }
    }



    public void removePiece(Position position) {
        Piece piece = getPiece(position);
        if (piece != null) {
            if (piece.getPlayerType() == PlayerType.PLAYER_1) {
                numOfPiecesP1--;
            } else {
                numOfPiecesP2--;
            }
            numberOfTotalPiecesPlaced--;
            board[position.getX()][position.getY()] = null;
        }
        mills.removeIf(mill -> mill.isBroken(this));
    }
    public boolean formMill(Position position) {
        Piece piece = getPiece(position);
        if (piece == null) {
            return false;
        }

        PlayerType playerType = piece.getPlayerType();

        // Check all possible mills.
        List<int[][]> possibleMills = getPossibleMills(position);
        for (int[][] millPositions : possibleMills) {
            List<Position> positionsInMill = Arrays.stream(millPositions)
                    .map(pos -> new Position(pos[0], pos[1]))
                    .collect(Collectors.toList());
            if (isMill(positionsInMill)) {
                Mill newMill = new Mill(positionsInMill);
                if (!mills.contains(newMill)) {
                    mills.add(newMill);
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isMill(List<Position> positions) {
        if (positions.size() != 3) return false;

        Piece firstPiece = getPiece(positions.get(0));
        if (firstPiece == null) return false;  // 如果第一个位置上没有棋子，则不能形成磨坊

        PlayerType playerType = firstPiece.getPlayerType();
        for (int i = 1; i < 3; i++) {
            Piece piece = getPiece(positions.get(i));
            if (piece == null || piece.getPlayerType() != playerType) {
                return false;  // 如果其他位置上没有棋子，或者棋子属于另一个玩家，则不能形成磨坊
            }
        }

        return true;  // 所有位置上都有同一个玩家的棋子，所以形成了磨坊
    }


    public boolean isValidMove(Move move, boolean allowFlying) {
        if (getPiece(move.getTargetPosition()) != null) {
            // There's already a piece at the destination position
            return false;
        }

        if (allowFlying) {
            // In the flying phase, any move is valid as long as the destination is empty
            return true;
        }

        // In the moving phase, the move is valid if the destination position is adjacent to the starting position
        return adjacentPositions.get(move.getStartPosition()).contains(move.getTargetPosition());
    }

    public List<Position> getOpponentNonMillPieces(PlayerType opponentType) {//获取对手的所有棋子的位置，并确定这些棋子是否在磨坊中
        List<Position> opponentPieces = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getPlayerType() == opponentType) {
                    Position position = new Position(i, j);
                    if (!isInMill(position)) {
                        opponentPieces.add(position);
                    }
                }
            }
        }
        return opponentPieces;
    }
    public boolean isInMill(Position position) {//确定某个位置是否在磨坊中
        for (Mill mill : mills) {
            if (mill.contains(position)) {
                return true;
            }
        }
        return false;
    }
    private List<int[][]> getPossibleMills(Position position) {
        // This method returns all the possible mills that could contain the given position.
        List<int[][]> possibleMills = new ArrayList<>();
        for (int[][] millPositions : allPossibleMills) {
            for (int[] millPosition : millPositions) {
                if (millPosition[0] == position.getX() && millPosition[1] == position.getY()) {
                    possibleMills.add(millPositions);
                    break;
                }
            }
        }
        return possibleMills;
    }


    public int getNumOfPiecesP1() {
        return numOfPiecesP1;
    }

    public int getNumOfPiecesP2() {
        return numOfPiecesP2;
    }

    public int getNumberOfTotalPiecesPlaced() {
        return numberOfTotalPiecesPlaced;
    }

    public List<Position> getEmptyPositions() { //获取所有空位置
        List<Position> emptyPositions = new ArrayList<>();
        for (int[] pos : VALID_POSITIONS) {
            Position position = new Position(pos[0], pos[1]);
            if (getPiece(position) == null) {
                emptyPositions.add(position);
            }
        }
        return emptyPositions;
    }
    public List<Position> getPieces(PlayerType playerType) { //获取某一玩家所有棋子的位置
        List<Position> playerPieces = new ArrayList<>();
        for (int[] pos : VALID_POSITIONS) {
            Position position = new Position(pos[0], pos[1]);
            Piece piece = getPiece(position);
            if (piece != null && piece.getPlayerType() == playerType) {
                playerPieces.add(position);
            }
        }
        return playerPieces;
    }
    public List<Position> getValidMoves(Position position) { //获取某一棋子的所有合法移动位置
        return adjacentPositions.get(position).stream()
                .filter(targetPosition -> getPiece(targetPosition) == null)
                .collect(Collectors.toList());
    }



}


