package gs.service;

import gs.service.cases.Case;

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
    private Case activeCase;

    public Player(long id, String displayName, String discriminator) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 5;
        this.money = 300;
        this.energy = 5;

        this.inventory = new LinkedHashMap<>();
        this.statistics = new PlayerStatistics();
        this.farm = new Farm(this);
        this.activeCase = null;
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

    public Case getActiveCase() {
        return activeCase;
    }

    public void updateLevel(int x) {
        level += x;
    }

    public void updateMoney(int x) {
        money += x;
    }

    public void updateEnergy(int x) {
        energy += x;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean setActiveCase(Case newCase) {
        if (activeCase == null) {
            activeCase = newCase;
            return true;
        } else {
            return false;
        }
    }

    public void clearCase() {
        activeCase = null;
    }

    @Override
    public String toString() {
        return "Player:" + displayName + "#" + discriminator;
    }
}
