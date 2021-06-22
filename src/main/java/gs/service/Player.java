package gs.service;

import gs.DataBase;
import gs.service.cases.Case;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Player {
    private final long id;
    private final String displayName;
    private final String discriminator;
    private int level;
    private int money;
    private int energy;
    private DataBase db;

    public LinkedHashMap <String, Integer> inventory;
    public PlayerStatistics statistics;
    public Farm farm;
    private Case activeCase;

    public Player(long id, String displayName, String discriminator, DataBase db) {
        this.id = id;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.level = 1;
        this.money = 300;
        this.energy = 5;
        this.db = db;

        this.inventory = new LinkedHashMap<>();
        this.statistics = new PlayerStatistics();
        this.farm = new Farm(this);
        this.activeCase = null;

        Thread energyGainer = new Thread(() -> {
            while (true) {
                if (energy < getMaxEnergy()) {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                        System.out.println(displayName + " restored 1 energy.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    energy++;
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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

        if (db != null) {
            db.updatePlayer(this);
        }
    }

    public void updateEnergy(int x) {
        energy += x;

        if (db != null) {
            db.updatePlayer(this);
        }
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
