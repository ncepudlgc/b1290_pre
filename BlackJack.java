import java.util.ArrayList;
import java.util.Random;

public class BlackJack {
    private GameFrame frame;
    private ArrayList<Card> deck;
    private Random random = new Random();

    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    private boolean gameActive = true;
    private int playerBalance = 10000;
    private final int COURTESY_AMOUNT = 1000;
    private int currentBet = 0;

    public BlackJack() {
        startGame();
        frame = new GameFrame(this);
        frame.initialize();
    }

    public void startGame() {
        resetGameState();
    }

    public void resetGameState() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        currentBet = 0;
    }

    public void dealInitialCards() {
        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    public void playerHit() {
        Card card = deck.remove(deck.size()-1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);
    }

    public void dealerPlay() {
        gameActive = false;
        while (dealerSum < 17) {
            Card card = deck.remove(deck.size()-1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerSum = reduceDealerAce();
            dealerHand.add(card);
        }
    }

    public void placeBet(int amount) {
        if (amount > 0 && amount <= playerBalance) {
            currentBet = amount;
            playerBalance -= amount;
        }
    }

    public void settleBet() {
        int playerFinalSum = reducePlayerAce();
        int dealerFinalSum = reduceDealerAce();
        boolean playerHasBJ = hasBlackjack(playerHand);
        boolean dealerHasBJ = hasBlackjack(dealerHand);

        if (playerFinalSum > 21 || dealerHasBJ && !playerHasBJ) {
        } else if (playerHasBJ && !dealerHasBJ) {
            playerBalance += (int) (currentBet * 2.5);
        } else if (dealerFinalSum > 21 || playerFinalSum > dealerFinalSum) {
            playerBalance += currentBet * 2;
        } else if (playerFinalSum == dealerFinalSum) {
            playerBalance += currentBet;
        }

        if (playerBalance <= 0) {
            playerBalance = COURTESY_AMOUNT;
            System.out.println("You have been rewarded courtesy yen! Thank you, very nice.");
        }
    }

    public boolean hasBlackjack(ArrayList<Card> hand) {
        if (hand.size() != 2) return false;
        Card card1 = hand.get(0);
        Card card2 = hand.get(1);
        boolean hasAce = card1.isAce() || card2.isAce();
        boolean hasTen = (card1.getValue() == 10) || (card2.getValue() == 10);
        return hasAce && hasTen;
    }

    public void restartGame() {
        gameActive = true;
        resetGameState();
    }

    public void addMoney(int amount) {
        playerBalance += amount;
    }

    public boolean isPlayerBusted() {
        return reducePlayerAce() > 21;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public Card getHiddenCard() {
        return hiddenCard;
    }

    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public int getDealerSum() {
        return reduceDealerAce();
    }

    public int getPlayerSum() {
        return reducePlayerAce();
    }

    public int getPlayerBalance() {
        return playerBalance;
    }

    public int getCurrentBet() {
        return currentBet;
    }
}