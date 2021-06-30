package gs.service.items;

import gs.service.Player;

public abstract class Item {
    protected String type;
    protected String name;
    protected String description;
    protected int price;
    protected int requiredLevel;

    public Item(String type, String name, String description, int price, int requiredLevel) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.price = price;
        this.requiredLevel = requiredLevel;
    }

    /**
     * Applies certain effect to the player according to the item type
     * @param player Affected player
     * @return Text response, that will be sent to player's console
     */
    public abstract String use(Player player);

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
