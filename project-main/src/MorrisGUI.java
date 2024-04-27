import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MorrisGUI extends JFrame {
    private Game game;
    private BoardPanel boardPanel;
    private JTextArea infoTextArea;

    public MorrisGUI(String player1Name, String player2Name,boolean playWithAI) {
        setTitle("Nine Men's Morris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 1050); // 设置初始大小
        setMinimumSize(new Dimension(800, 850)); // 设置最小大小
        setLocationRelativeTo(null);

        //game = new Game("Player 1", "Player 2");
        game = new Game(player1Name, player2Name, playWithAI);
        game.start();

        initUI();
        setVisible(true);

        // 添加组件适配器以确保棋盘保持正方形
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(getWidth(), getHeight() - 50);
                boardPanel.setPreferredSize(new Dimension(size, size));
                boardPanel.revalidate();
            }
        });
    }
    public MorrisGUI() {
        super();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel(game, this);
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);

        JButton endButton = new JButton("End");
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        controlPanel.add(endButton);

        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setPreferredSize(new Dimension(50, 40)); // 设置文本框为正方形
        infoTextArea.setFont(new Font("Arial", Font.BOLD, 20)); // 设置文字放大并加深
        updateInfoText();
        add(infoTextArea, BorderLayout.NORTH);
    }


    public void updateInfoText() {
        String playerName = game.getCurrentPlayer().getName();
        String playerColor = game.getCurrentPlayer().getColor() == Color.darkGray ? "Dark Gray" : "Light Gray";
        int turn = game.getTurn(); // 获取当前回合数
        int phase = game.getPhase(); // 获取当前游戏阶段
        String gamePhase;
        if( phase == 0){
             gamePhase = "Placement";
        }else{
            gamePhase = "Moving ";
        }


        infoTextArea.setText("Turn to " +  ": " + playerName + " (" + playerColor + ") Game Phase: " + gamePhase);
        infoTextArea.setForeground(game.getCurrentPlayer().getColor());
    }
    public class GameModeSelection extends JFrame {
        public GameModeSelection() {
            setTitle("Nine Men's Morris - Select Game Mode");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(500, 300);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 75, 80, 75));

            JButton playerVsPlayerButton = new JButton("Player vs Player");
            playerVsPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MorrisGUI game = new MorrisGUI("Player 1", "Player 2", false);
                    game.setVisible(true);
                    setVisible(false); // 关闭选择模式的界面
                }
            });
            playerVsPlayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(playerVsPlayerButton);

            buttonPanel.add(Box.createRigidArea(new Dimension(0, 50))); // 添加间距

            JButton playerVsAIButton = new JButton("Player vs AI");
            playerVsAIButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MorrisGUI game = new MorrisGUI("Player", "AI", true);
                    game.setVisible(true);
                    setVisible(false); // 关闭选择模式的界面
                }
            });
            playerVsAIButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(playerVsAIButton);

            buttonPanel.add(Box.createRigidArea(new Dimension(0, 50))); // 添加间距

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(exitButton);

            add(buttonPanel, BorderLayout.CENTER);
        }

/*        public static void main(String[] args) {
            MorrisGUI gui = new MorrisGUI();
            MorrisGUI.GameModeSelection selection = gui.new GameModeSelection();
            selection.setVisible(true);
        }*/
    }


   public static void main(String[] args) {
       MorrisGUI gui = new MorrisGUI();
       MorrisGUI.GameModeSelection selection = gui.new GameModeSelection();
       selection.setVisible(true);
    }
}







