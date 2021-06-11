package gs.service.items;

public abstract class Item {
    protected String type;
    protected String name;
    protected String description;
    protected int price;
    protected int requiredLevel;

    public String getType() {
        return type;
    }

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
}
