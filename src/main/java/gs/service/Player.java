package gs.service;

import java.util.*;

public class Player {
    private final long id;
    private final String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;

    public Map<String, Integer> inventory;
    public Farm farm;
    public PlayerStatistics statistics;

    public Player(long id, String displayName, String discriminator) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 1;
        this.money = 200;
        this.energy = 5;

        this.inventory = new LinkedHashMap<>();
        this.farm = new Farm(this);
        this.statistics = new PlayerStatistics();
//        inventory.put(new EnergySupply("Coffee", 1), 2);
    }

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
