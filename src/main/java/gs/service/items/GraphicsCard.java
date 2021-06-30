package gs.service.items;

public class GraphicsCard extends Item {
    private final int efficiency;

    public GraphicsCard(String type, String name, int price, int requiredLevel, int efficiency) {
        super(type, name, "Produces " + efficiency + " per minute", price, requiredLevel);
        this.efficiency = efficiency;
    }

    public int getEfficiency() {
        return efficiency;
    }
}
