package gs.service.items;

public class Item implements Comparable<Item> {
    protected String name;
    protected String description;
    protected int price;
    protected int requiredLevel;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @Override
    public int compareTo(Item o) {
        return name.compareTo(o.getName());
    }
}
