package gs.service;

import java.util.*;

public class Player {
    private final long id;
    private final String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;
    public LinkedHashMap <String, Integer> inventory;
    public PlayerStatistics statistics;
    public Farm farm;

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Player(long id, String displayName, String discriminator) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 5;
        this.money = 300;
        this.energy = 5;

        this.inventory = new LinkedHashMap<>();
        this.statistics = new PlayerStatistics(0,0,0,0);

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
