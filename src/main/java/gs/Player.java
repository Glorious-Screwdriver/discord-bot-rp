package gs;

import java.util.*;

public class Player {
    private final long id;
    private String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;
    public Map<Item, Integer> inventory;
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
        level = 1;
        money = 100;
        energy = 5;
        inventory = new HashMap<>();

        // tests
        inventory.put(new EnergySupply("Coffee", 1), 2);
    }

    public void updateEnergy(int x) {
        energy += x;
    }

    @Override
    public String toString() {
        return "Player:" + displayName + "#" + discriminator;
    }
}
