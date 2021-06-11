package gs.service.items;

public class GraphicsCard extends Item {
    private final int efficiency;

    public GraphicsCard(String type, String name, int price, int requiredLevel, int efficiency) {
        this.type = type;
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.efficiency = efficiency;
        this.description = "Produces " + efficiency + " per minute";
    }

    public int getEfficiency() {
        return efficiency;
    }
}
