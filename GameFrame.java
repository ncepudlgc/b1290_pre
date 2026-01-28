import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    private BlackJack game;
    private GamePanel gamePanel;
    private JPanel buttonPanel;
    private JButton hitButton;
    private JButton stayButton;
    
    private JPanel bettingOverlay;
    private JTextField betAmountField;
    private JButton placeBetButton;
    private JLabel balanceLabel;
    private JLabel errorLabel;
    
    private int boardWidth = 600;
    private int boardHeight = 620;

    public GameFrame(BlackJack game) {
        super("Black Jack");
        this.game = game;
    }

    public void initialize() {
        initBettingOverlay();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }

    private void initBettingOverlay() {
        bettingOverlay = new JPanel();
        bettingOverlay.setLayout(new BoxLayout(bettingOverlay, BoxLayout.Y_AXIS));
        bettingOverlay.setBackground(new Color(0, 0, 0, 180));
        bettingOverlay.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Create betting form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(53, 101, 77));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel titleLabel = new JLabel("Place Your Bet");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        balanceLabel = new JLabel("Balance: ¥" + game.getPlayerBalance());
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Bet amount input
        JLabel betLabel = new JLabel("Enter bet amount:");
        betLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        betLabel.setForeground(Color.WHITE);
        betLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        betAmountField = new JTextField(10);
        betAmountField.setFont(new Font("Arial", Font.PLAIN, 16));
        betAmountField.setMaximumSize(new Dimension(200, 30));
        betAmountField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Quick bet buttons
        JPanel betButtonPanel = new JPanel(new FlowLayout());
        betButtonPanel.setOpaque(false);
        placeBetButton = new JButton("Place Bet");
        betButtonPanel.add(placeBetButton);
        
        JPanel quickBetPanel = new JPanel(new FlowLayout());
        quickBetPanel.setOpaque(false);
        JButton quick500 = new JButton("500");
        JButton quick1000 = new JButton("1000");
        JButton quick2000 = new JButton("2000");
        JButton quickAllIn = new JButton("All In");
        quickBetPanel.add(quick500);
        quickBetPanel.add(quick1000);
        quickBetPanel.add(quick2000);
        quickBetPanel.add(quickAllIn);
        
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(balanceLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(betLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(betAmountField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(quickBetPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(betButtonPanel);
        
        bettingOverlay.add(Box.createVerticalGlue());
        bettingOverlay.add(formPanel);
        bettingOverlay.add(Box.createVerticalGlue());
        
        setupBettingOverlayListeners(quick500, quick1000, quick2000, quickAllIn);
    }

    private void setupBettingOverlayListeners(JButton quick500, JButton quick1000, JButton quick2000, JButton quickAllIn) {
        placeBetButton.addActionListener(e -> {
            try {
                String text = betAmountField.getText().trim();
                if (text.isEmpty()) {
                    showBetError("Please enter a bet amount");
                    return;
                }
                int amount = Integer.parseInt(text);
                if (validateBet(amount)) {
                    game.placeBet(amount);
                    hideBettingOverlay();
                    startGameWithBet();
                }
            } catch (NumberFormatException ex) {
                showBetError("Please enter a valid number");
            }
        });
        
        quick500.addActionListener(e -> betAmountField.setText("500"));
        quick1000.addActionListener(e -> betAmountField.setText("1000"));
        quick2000.addActionListener(e -> betAmountField.setText("2000"));
        quickAllIn.addActionListener(e -> betAmountField.setText(String.valueOf(game.getPlayerBalance())));
        
        // Enter key to place bet
        betAmountField.addActionListener(e -> placeBetButton.doClick());
    }
    
    private boolean validateBet(int amount) {
        if (amount <= 0) {
            showBetError("Bet amount must be greater than 0");
            return false;
        }
        if (amount > game.getPlayerBalance()) {
            showBetError("Insufficient balance");
            return false;
        }

        clearBetError();
        return true;
    }
    
    private void showBetError(String message) {
        errorLabel.setText(message);
    }
    
    private void clearBetError() {
        errorLabel.setText(" ");
    }
    
    private void showBettingOverlay() {
        balanceLabel.setText("Balance: ¥" + game.getPlayerBalance());
        betAmountField.setText("");
        clearBetError();
        bettingOverlay.setVisible(true);
        betAmountField.requestFocus();
        
        setComponentZOrder(bettingOverlay, 0);
        revalidate();
        repaint();
    }
    
    private void hideBettingOverlay() {
        bettingOverlay.setVisible(false);
        revalidate();
        repaint();
        requestFocus();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        gamePanel = new GamePanel(game, this);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        buttonPanel = new JPanel();
        hitButton = new JButton("Hit");
        stayButton = new JButton("Stay");
        hitButton.setFocusable(false);
        stayButton.setFocusable(false);
        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel and overlay layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(boardWidth, boardHeight));
        mainPanel.setBounds(0, 0, boardWidth, boardHeight-35);
        bettingOverlay.setBounds(0, 0, boardWidth, boardHeight);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(bettingOverlay, JLayeredPane.PALETTE_LAYER);
        add(layeredPane, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.playerHit();
                if (game.isPlayerBusted()) {
                    endGame();
                } else {
                    gamePanel.repaint();
                }
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                endGame();
            }
        });
        
        setFocusable(true);
    }
    
    private void startGameWithBet() {
        if (game.getCurrentBet() > 0) {
            game.dealInitialCards();
            buttonPanel.setVisible(true);
            hitButton.setEnabled(true);
            stayButton.setEnabled(true);
            gamePanel.repaint();
        }
    }

    private void setupFrame() {
        setVisible(true);
        setSize(boardWidth, boardHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.repaint();
    }

    private void endGame() {
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        game.dealerPlay();
        game.settleBet();
        gamePanel.repaint();
        gamePanel.startEndGameSequence();
    }

    public void restartGame() {
        game.restartGame();
        showBettingOverlay();
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        gamePanel.repaint();
    }
}