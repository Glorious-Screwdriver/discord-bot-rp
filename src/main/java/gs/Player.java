package gs;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final long id;
    private String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;
    private int maxEnergy;
    public List<Item> inventory;
    PlayerStatistics statistics;

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
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
        return maxEnergy;
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public Player(long id, String displayName, String discriminator) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        level = 1;
        money = 100;
        energy = maxEnergy = 5;
        inventory = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Player:" + displayName + "#" + discriminator;
    }
}
