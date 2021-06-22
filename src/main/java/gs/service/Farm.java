package gs.service;

import gs.service.items.GraphicsCard;

import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Farm {
    public List<GraphicsCard> cards;

    Player player;
    int income;
    Thread thread;

    public Farm(Player player) {
        this.player = player;
        this.cards = new ArrayList<>();
        this.income = 0;
    }

    public void start() {
        thread = thread();
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public int getIncome() {
        return income;
    }

    public int getLimit() {
        int t = this.player.getLevel() - 1;
        return Math.max(t, 0);
    }

    /**
     * Adds a card to the player's farm
     *
     * @param card Graphics card you want to add
     * @return true - if card is added, false - if there is no space for a new card
     */
    public boolean addCard(GraphicsCard card) {
        if (cards.size() < getLimit()) {
            cards.add(card);
            income += card.getEfficiency();
            return true;
        } else {
            return false;
        }
    }

    public boolean addCards(ArrayList<GraphicsCard> cards) {
        boolean b = true;
        for (GraphicsCard card : cards) {
            b &= addCard(card);
        }
        return b;
    }

    public void removeCard(GraphicsCard card) {
        income -= card.getEfficiency();
        cards.remove(card);
    }

    private Thread thread() {
        return new Thread(() -> {
            Random random = new Random();

            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(player.getDisplayName() + "'s farm stopped.");
                    break;
                }

                int profit = 0;
                for (GraphicsCard card : cards) {
                    profit += (int) Math.round(card.getEfficiency() * (random.nextDouble() + 0.5));
                }

                player.updateMoney(profit);
                System.out.println(player.getDisplayName() + "'s farm has produced " + profit);
            }

        });
    }
}
