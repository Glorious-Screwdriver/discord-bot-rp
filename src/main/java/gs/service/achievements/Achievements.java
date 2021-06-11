package gs.service.achievements;

import gs.service.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Achievements {

    public static List<Achievement> calculate(Player player) {
        return Arrays.asList(
                spender(player),
                worker(player),
                coffeeEnjoyer(player),
                energizer(player)
        );
    }

    public static Achievement spender(Player player) {

        return new Achievement("Spender", "So you like to spend money...") {
            @Override
            public String getLevel() {
                int moneySpent = player.statistics.getMoneySpent();

                if (moneySpent >= 10000) {
                    return "pro";
                } else if (moneySpent >= 5000) {
                    return "average";
                } else if (moneySpent >= 1000) {
                    return "noob";
                }

                return null;
            }
        };

    }

    public static Achievement worker(Player player) {

        return new Achievement("Worker", "Working hard I see, well done!") {
            @Override
            public String getLevel() {
                int casesDone = player.statistics.getCasesDone();

                if (casesDone >= 100) {
                    return "pro";
                } else if (casesDone >= 50) {
                    return "average";
                } else if (casesDone >= 10) {
                    return "noob";
                }

                return null;
            }
        };

    }

    public static Achievement coffeeEnjoyer(Player player) {

        return new Achievement("Coffee enjoyer", "Coffee is addicting sometimes, isn't it?") {
            @Override
            public String getLevel() {
                int coffeeConsumed = player.statistics.getCoffeeConsumed();

                if (coffeeConsumed >= 20) {
                    return "pro";
                } else if (coffeeConsumed >= 10) {
                    return "average";
                } else if (coffeeConsumed >= 5) {
                    return "noob";
                }

                return null;
            }
        };

    }

    public static Achievement energizer(Player player) {

        return new Achievement("Energizer", "You have the energy of a whole factory!") {
            @Override
            public String getLevel() {
                int energyDrinksConsumed = player.statistics.getEnergyDrinksConsumed();

                if (energyDrinksConsumed >= 20) {
                    return "pro";
                } else if (energyDrinksConsumed >= 10) {
                    return "average";
                } else if (energyDrinksConsumed >= 5) {
                    return "noob";
                }

                return null;
            }
        };

    }
}
