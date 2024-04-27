import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class BoardPanel extends JPanel {
    private Board board;
    private Game game;
    private Position selectedPosition;
    private int X_SCALE;
    private int  Y_SCALE;
    private static final int RADIUS = 20;
    private static final int MARGIN = 50; // 添加边距
    private static final int CLICK_TOLERANCE = 10;//表示点击容差
    private MorrisGUI gui;

    public BoardPanel(Game game, MorrisGUI gui) {
        this.board = game.getBoard();
        this.game = game;
        this.gui = gui;
        this.selectedPosition = null;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 获取面板宽度和高度，然后计算 X_SCALE 和 Y_SCALE
        int panelWidth = getWidth() - 2 * MARGIN;
        int panelHeight = getHeight() - 2 * MARGIN;
        X_SCALE = panelWidth / 10;
        Y_SCALE = panelHeight / 10;

        g.translate(MARGIN, MARGIN); // 添加平移，使棋盘保持边距
        drawBoard(g);
        drawPoints(g);
        drawPieces(g);
        if (selectedPosition != null) {
            drawSelectedPosition(g);
        }
    }


    private void drawBoard(Graphics g) {
        g.drawLine(X_SCALE/5, Y_SCALE/5, 7 * X_SCALE, Y_SCALE/5);
        g.drawLine(X_SCALE/5, Y_SCALE/5, X_SCALE/5, 7 * Y_SCALE);
        g.drawLine(7 * X_SCALE, Y_SCALE/5, 7 * X_SCALE, 7 * Y_SCALE);
        g.drawLine(X_SCALE/5, 7 * Y_SCALE, 7 * X_SCALE, 7 * Y_SCALE);

        // Draw the outer square
        g.drawLine(X_SCALE, Y_SCALE, 6 * X_SCALE, Y_SCALE);
        g.drawLine(X_SCALE, Y_SCALE, X_SCALE, 6 * Y_SCALE);
        g.drawLine(6 * X_SCALE, Y_SCALE, 6 * X_SCALE, 6 * Y_SCALE);
        g.drawLine(X_SCALE, 6 * Y_SCALE, 6 * X_SCALE, 6 * Y_SCALE);

      // Draw the middle square
       g.drawLine(2 * X_SCALE, 2 * Y_SCALE, 5 * X_SCALE, 2 * Y_SCALE);
       g.drawLine(2 * X_SCALE, 2 * Y_SCALE, 2 * X_SCALE, 5 * Y_SCALE);
       g.drawLine(5 * X_SCALE, 2 * Y_SCALE, 5 * X_SCALE, 5 * Y_SCALE);
       g.drawLine(2 * X_SCALE, 5 * Y_SCALE, 5 * X_SCALE, 5 * Y_SCALE);

       // Draw the inner square
       g.drawLine(3 * X_SCALE, 3 * Y_SCALE, 4 * X_SCALE, 3 * Y_SCALE);
       g.drawLine(3 * X_SCALE, 3 * Y_SCALE, 3 * X_SCALE, 4 * Y_SCALE);
       g.drawLine(4 * X_SCALE, 3 * Y_SCALE, 4 * X_SCALE, 4 * Y_SCALE);
       g.drawLine(3 * X_SCALE, 4 * Y_SCALE, 4 * X_SCALE, 4 * Y_SCALE);

       // Draw the lines connecting the squares
       g.drawLine(3 * X_SCALE, Y_SCALE/5, 3 * X_SCALE, 4 * Y_SCALE);
       g.drawLine(4 * X_SCALE, Y_SCALE/5, 4 * X_SCALE, 4 * Y_SCALE);
       g.drawLine(3 * X_SCALE, 4 * Y_SCALE, 3 * X_SCALE, 7 * Y_SCALE);
       g.drawLine(4 * X_SCALE, 4 * Y_SCALE, 4 * X_SCALE, 7 * Y_SCALE);

       g.drawLine(X_SCALE/5, 3 * Y_SCALE, 4 * X_SCALE, 3 * Y_SCALE);
       g.drawLine(X_SCALE/5, 4 * Y_SCALE, 4 * X_SCALE, 4 * Y_SCALE);
       g.drawLine(4 * X_SCALE, 3 * Y_SCALE, 7 * X_SCALE, 3 * Y_SCALE);
       g.drawLine(4 * X_SCALE, 4 * Y_SCALE, 7 * X_SCALE, 4 * Y_SCALE);
    }

    private void drawPoints(Graphics g) {
        g.setColor(Color.BLACK);
        for (int[] pos : board.getValidPositions()) {
            int x = pos[0];
            int y = pos[1];
            g.fillOval(x * X_SCALE + X_SCALE / 2 - 5, y * Y_SCALE + Y_SCALE / 2 - 5, 5, 5);
        }
    }

    private void drawPieces(Graphics g) {
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                Position position = new Position(x, y);
                if (board.isValidPosition(position)) {
                    Piece piece = board.getPiece(position);
                    if (piece != null) {
                        g.setColor(piece.getColor());
                        g.fillOval(x * X_SCALE + X_SCALE / 2 - RADIUS / 2, y * Y_SCALE + Y_SCALE / 2 - RADIUS / 2, RADIUS, RADIUS);
                        g.setColor(Color.BLACK);
                        g.drawOval(x * X_SCALE + X_SCALE / 2 - RADIUS / 2, y * Y_SCALE + Y_SCALE / 2 - RADIUS / 2, RADIUS, RADIUS);
                    }
                }
            }
        }
    }
    //method to draw the selected position with a highlighted border
    private void drawSelectedPosition(Graphics g) {
        int x = selectedPosition.getX();
        int y = selectedPosition.getY();
        g.setColor(Color.red);
        g.drawOval(x * X_SCALE + X_SCALE / 2 - RADIUS / 2 - 5, y * Y_SCALE + Y_SCALE / 2 - RADIUS / 2 - 5, RADIUS + 10, RADIUS + 10);
    }


    private void handleClick(int x, int y) {
        int gridX = (x - MARGIN + X_SCALE / 2) / X_SCALE;
        int gridY = (y - MARGIN + Y_SCALE / 2) / Y_SCALE;

        int offsetX = (x - MARGIN) % X_SCALE;
        int offsetY = (y - MARGIN) % Y_SCALE;

        System.out.println("Clicked on pixel: (" + x + ", " + y + ")");
        System.out.println("Clicked on grid: (" + gridX + ", " + gridY + ")");
        System.out.println("Offset: (" + offsetX + ", " + offsetY + ")");

//        // 检查点击位置与棋盘点的距离是否在容差范围内
//        if (Math.abs(offsetX - X_SCALE / 2) > CLICK_TOLERANCE || Math.abs(offsetY - Y_SCALE / 2) > CLICK_TOLERANCE) {
//            System.out.println("Click is outside of tolerance.");
//            return;
//        }



        Position clickedPosition = new Position(gridX, gridY);

        System.out.println("Clicked position is valid: " + game.getBoard().isValidPosition(clickedPosition));

        if (game.getBoard().isValidPosition(clickedPosition)) {
            Piece piece = game.getBoard().getPiece(clickedPosition);
            System.out.println("Piece at clicked position: " + (piece == null ? "null" : piece.getPlayerType()));

            // Check which phase the game is in and handle accordingly
            System.out.println("Current game phase: " + game.getPhase());

            // Only switch between placement and moving phase in the game
            switch (game.getPhase()) {
                case 0: // Placement phase
                    handlePlacementPhase(clickedPosition, piece);
                    break;
                case 1: // Moving phase (including flying)
                    handleMovingOrFlyingPhase(clickedPosition, piece);
                    break;
                default:
                    break;
            }
            // After handling the move, if it's the AI's turn, let the AI make a move
            if (game.isPlayingWithAI && game.getCurrentPlayer().getPlayerType() == PlayerType.AI) {
                game.makeMove();
                game.removePiece();
                game.nextPlayer();
            }
            repaint();
        }
    }




    private void handlePlacementPhase(Position position, Piece piece) {
        System.out.println("Handling placement phase. Position: " + position + " Piece: " + (piece == null ? "null" : piece.getPlayerType()));

        if (game.inRemoveMode()) {
            System.out.println("Game is in remove mode.");
            handleRemovePiece(position, piece);
        } else if (piece == null) {  // 如果该位置为空
            // 打印当前玩家
            System.out.println("Current player before making a move: " + game.getCurrentPlayer().getPlayerType());
            // 创建一个Move对象，起始位置可以设置为null，因为放置阶段没有起始位置的概念
            // 如果是人类玩家的回合或者并非AI模式
            if (!game.isPlayingWithAI || game.getCurrentPlayer().getPlayerType() != PlayerType.AI) {
            Move move = new Move(null, position, game.getCurrentPlayer().getPlayerType());
            System.out.println("Trying to make move: " + move);
            // 使用makeMove方法尝试放置一枚棋子
                if (game.applyMove(move)) {
                    System.out.println("Move made successfully.");
                    // 检查是否形成了磨坊
                    if (game.getBoard().formMill(position)) {
                        System.out.println("Mill formed. Setting remove mode.");
                        game.setRemoveMode(true);
                        gui.updateInfoText(); // 更新信息文本框
                        // 如果当前玩家是AI，触发AI移除棋子
                        if (game.isPlayingWithAI && game.getCurrentPlayer().getPlayerType() == PlayerType.AI) {
                            Position removePos = ((AIPlayer) game.getCurrentPlayer()).getRandomRemovePiece(game.getBoard());
                            game.applyRemovePiece(removePos); // AI removes a piece
                            gui.updateInfoText(); // 更新信息文本框
                            game.setRemoveMode(false); // Exit remove mode
                            game.nextPlayer(); // Proceed to next player
                        }
                    } else {
                        System.out.println("No mill formed. Moving to next player.");
                        game.nextPlayer(); // 如果没有形成磨坊，换下一个玩家
                        gui.updateInfoText(); // 更新信息文本框
                        // 如果当前玩家是AI，触发AI行动
                        if (game.isPlayingWithAI && game.getCurrentPlayer().getPlayerType() == PlayerType.AI) {
                            game.makeMove();
                            game.removePiece();
                            game.nextPlayer(); // Proceed to next player
                        }
                    }
                } else {
                    System.out.println("Move failed.");
                }

            } }else {
            System.out.println("Position is not empty.");
        }
    }

    private void handleRemovePiece(Position position, Piece piece) {
        System.out.println("Handling remove piece. Position: " + position + " Piece: " + (piece == null ? "null" : piece.getPlayerType()));
        // 如果是人类玩家的回合或者并非AI模式
        if (!game.isPlayingWithAI || game.getCurrentPlayer().getPlayerType() != PlayerType.AI) {
            if (piece != null && piece.getPlayerType() != game.getCurrentPlayer().getPlayerType()) {
                // The user wants to remove opponent's piece

                // check if the piece is a valid choice
                if (game.isValidRemovePiece(position)) {
                    System.out.println("Removing opponent's piece at " + position);
                    game.applyRemovePiece(position);
                    gui.updateInfoText(); // 更新信息文本框
                    // Check if the next player is AI
                    PlayerType nextPlayerType = game.getNextPlayer().getPlayerType();
                    if (nextPlayerType == PlayerType.AI) {
                        // If the next player is AI, switch to AI
                        game.nextPlayer();
                        if (game.isPlayingWithAI) {
                            // If we are playing with AI, let the AI make a move
                            game.makeMove();
                            game.removePiece();
                            game.nextPlayer(); // 如果没有形成磨坊，换下一个玩家
                        }
                    }
                } else {
                    System.out.println("Invalid piece chosen to remove. Please choose a piece not in a mill or there are no pieces not in mills.");
                }
            } else {
                System.out.println("Not removing piece.");
            }
        }
    }


    private void handleMovingOrFlyingPhase(Position position, Piece piece) {
        System.out.println("handleMovingOrFlyingPhase called with position: " + position + " and piece: " + piece);
        if (game.inRemoveMode()) {
            handleRemovePiece(position, piece);
        } else {
            // 如果是人类玩家的回合或者并非AI模式
            if (!game.isPlayingWithAI || game.getCurrentPlayer().getPlayerType() != PlayerType.AI) {
                if (selectedPosition == null) {
                    // select a piece
                    if (piece != null && piece.getPlayerType() == game.getCurrentPlayer().getPlayerType()) {
                        selectedPosition = position;
                    }
                } else {
                    // try to move the selected piece
                    if (piece == null) {
                        Move move = new Move(selectedPosition, position, game.getCurrentPlayer().getPlayerType());
                        // check if the move is valid
                        if (game.getBoard().isValidMove(move, game.getCurrentPlayer().getPhase() == 1) && game.applyMove(move)) {
                            selectedPosition = null;
                            // check for mill
                            if (game.getBoard().formMill(position)) {
                                game.setRemoveMode(true);
                            } else {
                                game.nextPlayer(); // if no mill is formed, change the player
                                gui.updateInfoText(); // 更新信息文本框
                                if (game.isPlayingWithAI && game.getCurrentPlayer().getPlayerType() == PlayerType.AI) {
                                    game.makeMove();
                                    game.removePiece();
                                    game.nextPlayer(); // 如果没有形成磨坊，换下一个玩家
                                }
                            }
                        }
                    } else if (piece.getPlayerType() == game.getCurrentPlayer().getPlayerType()) {
                        // if the player clicked on his own piece, select it
                        selectedPosition = position;
                    }
                }
            }
        }
        if (game.isGameOver()) {
            Player winner = game.getWinner();
            JOptionPane.showMessageDialog(null,
                    "The winner is " + winner.getName() ,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }


}







