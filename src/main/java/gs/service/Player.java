package gs.service;

import gs.DataBase;
import gs.service.cases.Case;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Player {
    private final long id;
    private final String displayName;
    private final String discriminator;
    public LinkedHashMap<String, Integer> inventory;
    public PlayerStatistics statistics;
    public Farm farm;
    private int level;
    private int money;
    private int energy;
    private DataBase dataBase;
    private Case activeCase;

    public Player(long id, String displayName, String discriminator, DataBase dataBase) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 1;
        this.money = 300;
        this.energy = 5;
        this.dataBase = dataBase;

        this.inventory = new LinkedHashMap<>();
        this.statistics = new PlayerStatistics();
        this.farm = new Farm(this);
        this.activeCase = null;

        Thread energyGainer = new Thread(() -> {
            try {
                while (true) {
                    if (energy < getMaxEnergy()) {
                        TimeUnit.MINUTES.sleep(1);
                        if (energy < getMaxEnergy()) {
                            energy++;
                            System.out.println(displayName + " restored 1 energy.");
                        }
                    } else {
                        TimeUnit.SECONDS.sleep(5);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        energyGainer.start();
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

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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
        dataBase.updatePlayer(this);
        money += x;
    }

    public void updateEnergy(int x) {
        energy += x;
        dataBase.updatePlayer(this);
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
