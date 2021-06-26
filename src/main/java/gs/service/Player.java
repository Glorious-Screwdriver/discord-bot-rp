package gs.service;

import gs.DataBase;
import gs.service.cases.Case;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Player {
    private final long id;
    private final String displayName;
    private final String discriminator;

    private final DataBase db;

    private int level = 1;
    private int money = 300;
    private int energy = 5;

    public LinkedHashMap<String, Integer> inventory;
    public PlayerStatistics statistics;
    public Farm farm;
    private Case activeCase;

    Thread energyThread = null;

    public Player(long id, String displayName, String discriminator, DataBase db) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;

        this.db = db;

        this.inventory = new LinkedHashMap<>();
        this.statistics = new PlayerStatistics();
        this.farm = new Farm(this);
        this.activeCase = null;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
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

    public void updateLevel(int value) {
        level += value;
    }

    public void updateMoney(int value) {
        money += value;

        if (db != null) {
            db.updatePlayer(this);
        }
    }

    public void updateEnergy(int value) {
        energy += value;

        if (getEnergy() < getMaxEnergy()) {
            if (!energyThread.isAlive()) {
                startEnergyThread();
            }
        } else if (getEnergy() >= getMaxEnergy()) {
            if (energyThread.isAlive()) {
                stopEnergyThread();
            }
        }

        if (db != null) {
            db.updatePlayer(this);
        }
    }

    public void startEnergyThread() {
        energyThread = new Thread(() -> {
            System.out.println(displayName + "'s energy thread started.");

            while (energy < getMaxEnergy()) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
                energy++;
                System.out.println(displayName + " restored energy.");
            }

            System.out.println(displayName + "'s energy thread stopped.");
        });
        energyThread.start();
    }

    public void stopEnergyThread() {
        energyThread.interrupt();
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

    @Override
    public boolean equals(Object another) {
        Player anotherPlayer;
        if (another.getClass() == Player.class) {
            anotherPlayer = (Player) another;
            return this.getId() == anotherPlayer.getId();
        } else {
            throw new IllegalArgumentException("Object is not Player, can't compare");
        }
    }
}
