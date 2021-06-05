package gs;

import java.util.ArrayList;
import java.util.List;

public class Player {
    long id;
    String name;
    int level;
    int money;
    int energy;
    List<Item> inventory;
    public Console console;
    PlayerStatistics statistics;

    public Player(long id, String name, Console console) {
        this.id = id;
        this.name = name;
        this.console = console;
        level = 1;
        money = 100;
        energy = 5;
        inventory = new ArrayList<>();
    }

    public Player(long id, String name, int level, int money, int energy, List<Item> inventory, Console console) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.money = money;
        this.energy = energy;
        this.inventory = inventory;
        this.console = console;
    }

    @Override
    public String toString() {
        return "Player: " + name;
    }
}
