package gs.service;

import gs.service.items.GraphicsCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Farm {
    Player player;
    List<GraphicsCard> cards;
    int income;
    int limit;
    Thread calculateProfit;

    public Farm(Player player) {
        this.player = player;
        this.cards = new ArrayList<>();
        this.income = 3000;
        this.limit = 3;

        this.calculateProfit = new Thread(() -> {
            Random random = new Random();

            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                long profit = 0;
                for (GraphicsCard card : cards) {
                    boolean sign = random.nextBoolean();
                    // Элемент случайности
                    if (sign) {
                        profit += card.getEfficiency() - Math.round(card.getEfficiency() * random.nextDouble() / 2);
                    } else {
                        profit += card.getEfficiency() + Math.round(card.getEfficiency() * random.nextDouble() / 2);
                    }
                }
                player.updateMoney((int) profit);
                System.out.println(player.getDisplayName() + "'s farm has produced " + profit);
            }
        });
        calculateProfit.start();
    }

    public int getIncome() {
        return income;
    }

    public void updateLimit(int value) {
        this.limit += value;
    }

    /**
     * Adds a card to the player's farm
     * @param card Graphics card you want to add
     * @return true - if card is added, false - if there is no space for a new card
     */
    public boolean addCard(GraphicsCard card) {
        if (cards.size() < limit) {
            cards.add(card);
            income += card.getEfficiency();
            return true;
        } else {
            return false;
        }
    }

    public void removeCard(GraphicsCard card) {
        income -= card.getEfficiency();
        cards.remove(card);
    }
}