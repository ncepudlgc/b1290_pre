import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.geom.AffineTransform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel {
    private BlackJack game;
    private GameFrame gameFrame;
    private int cardWidth = 110;
    private int cardHeight = 154;
    
    private static final int ANIMATION_DURATION_MS = 3000;
    private static final int ROTATION_DELAY_MS = 1000;
    private static final int ROTATION_DURATION_MS = 300;
    private static final int FONT_SIZE = 48;
    private static final int SCORE_FONT_SIZE = 32;

    private boolean isAnimating = false;
    private long animationStartTime;
    private Timer delayTimer;
    private Timer animationTimer;

    public GamePanel(BlackJack game, GameFrame gameFrame) {
        this.game = game;
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(53, 101, 77));
        initTimers();
    }

    private void initTimers() {
        animationTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
                if (System.currentTimeMillis() - animationStartTime >= ANIMATION_DURATION_MS) {
                    stopAnimation();
                }
            }
        });
        animationTimer.setRepeats(true);
        
        delayTimer = new Timer(ROTATION_DELAY_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAnimation();
            }
        });
        delayTimer.setRepeats(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.drawString("Balance: ¥" + game.getPlayerBalance(), 20, getHeight() - 40);
            g2d.drawString("Bet: ¥" + game.getCurrentBet(), 20, getHeight() - 20);

            // Draw hidden card
            Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
            if (!game.isGameActive()) {
                hiddenCardImg = new ImageIcon(getClass().getResource(game.getHiddenCard().getImagePath())).getImage();
            }
            g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

            // Draw dealer's hand
            ArrayList<Card> dealerHand = game.getDealerHand();
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
            }

            // Draw player's hand
            ArrayList<Card> playerHand = game.getPlayerHand();
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
            }

            if (!game.isGameActive()) {
                int dealerSum = game.getDealerSum();
                int playerSum = game.getPlayerSum();

                String message = "";
                String winningsText = "";
                if (playerSum > 21) {
                    message = "Bust! You Lose!";
                    winningsText = "Lost: -¥" + game.getCurrentBet();
                } else if (dealerSum > 21) {
                    message = "Dealer Bust! You Win!";
                    winningsText = "Won: +¥" + game.getCurrentBet();
                } else if (playerSum == dealerSum) {
                    message = "Tie!";
                    winningsText = "Bet Returned";
                } else if (playerSum > dealerSum) {
                    if (game.hasBlackjack(game.getPlayerHand())) {
                        message = "Blackjack! You Win!";
                        winningsText = "Won: +¥" + (int) (game.getCurrentBet() * 1.5);
                    } else {
                        message = "You Win!";
                        winningsText = "Won: +¥" + game.getCurrentBet();
                    }
                } else {
                    message = "You Lose!";
                    winningsText = "Lost: -¥" + game.getCurrentBet();
                }

                String scoreText = playerSum + " - " + dealerSum;

                double rotationAngle = 0;
                if (isAnimating) {
                    long rotationElapsed = System.currentTimeMillis() - animationStartTime;
                    if (rotationElapsed <= ROTATION_DURATION_MS) {
                        double progress = (double) rotationElapsed / ROTATION_DURATION_MS;
                        rotationAngle = Math.PI * progress;
                    } else if (rotationElapsed <= ROTATION_DURATION_MS + ROTATION_DELAY_MS) {
                        rotationAngle = Math.PI;
                    } else if (rotationElapsed <= ROTATION_DELAY_MS + 2 * ROTATION_DURATION_MS) {
                        rotationElapsed -= ROTATION_DELAY_MS;
                        double progress = (double) rotationElapsed / ROTATION_DURATION_MS;
                        rotationAngle = Math.PI * (progress);
                    }
                }

                // Draw win message with rotation transformation
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.white);
                g2d.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
                FontMetrics messageFm = g2d.getFontMetrics();
                int messageX = (getWidth() - messageFm.stringWidth(message)) / 2;
                int messageY = getHeight() / 2 - 30;

                AffineTransform originalTransform = g2d.getTransform();
                g2d.translate(messageX + messageFm.stringWidth(message) / 2, messageY-FONT_SIZE/2);
                g2d.rotate(rotationAngle);
                g2d.translate(-messageFm.stringWidth(message) / 2, FONT_SIZE/2);
                g2d.drawString(message, 0, 0);
                g2d.setTransform(originalTransform);

                // Draw final score and earnings
                g2d.setFont(new Font("Arial", Font.PLAIN, SCORE_FONT_SIZE));
                FontMetrics scoreFm = g2d.getFontMetrics();
                int scoreX = (getWidth() - scoreFm.stringWidth(scoreText)) / 2;
                int scoreY = messageY + 40;
                g2d.drawString(scoreText, scoreX, scoreY);
                int winningsX = (getWidth() - scoreFm.stringWidth(winningsText)) / 2;
                int winningsY = scoreY + 30;
                g2d.drawString(winningsText, winningsX, winningsY);
            }
            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEndGameSequence() {
        if (!game.isGameActive() && !isAnimating) {
            delayTimer.start();
        }
    }
    
    private void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
        animationTimer.start();
    }
    
    private void stopAnimation() {
        animationTimer.stop();
        isAnimating = false;
        gameFrame.restartGame();
    }
}