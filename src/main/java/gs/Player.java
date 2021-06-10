package gs;

import java.util.*;

public class Player {
    private final long id;
    private String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;
    Map<String, Integer> inventory;
    PlayerStatistics statistics;

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public int getLevel() {
        return level;
    }

    public int getMoney() {
        return money;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return 4 + level;
    }

    public Player(long id, String displayName, String discriminator) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 1;
        this.money = 200;
        this.energy = 5;
        this.inventory = new LinkedHashMap<>();

        // tests
//        inventory.put(new EnergySupply("Coffee", 1), 2);
    }

    public void updateMoney(int x) {
        money += x;
    }

    public void updateEnergy(int x) {
        energy += x;
    }

    @Override
    public String toString() {
        return "Player:" + displayName + "#" + discriminator;
    }
}
