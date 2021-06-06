package gs;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public long id;
    public String name;
    public String discriminator;
    public int level;
    public int money;
    public int energy;
    public List<Item> inventory;
    public Console console;
    PlayerStatistics statistics;

    public Player(long id, String name, String discriminator, Console console) {
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
        this.console = console;
        level = 1;
        money = 100;
        energy = 5;
        inventory = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Player:" + name + "#" + discriminator;
    }
}
