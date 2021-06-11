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

    public GraphicsCard(String type) {
        switch (type) {
            case "graphics_card_1":
                this.type = "graphics_card_1";
                this.name = "GTX 680";
                this.price = 300;
                this.requiredLevel = 1;
                this.efficiency = 10;
                System.out.println("graphics_card_1");
                break;
            case "graphics_card_2":
                this.type = "graphics_card_2";
                this.name = "GTX 970";
                this.price = 1000;
                this.requiredLevel = 3;
                this.efficiency = 100;
                break;
            case "graphics_card_3":
                this.type = "graphics_card_3";
                this.name = "Titan Z";
                this.price = 5000;
                this.requiredLevel = 5;
                this.efficiency = 500;
                break;
            default:
                System.err.println(type+" Не найден");
                this.efficiency = 0;
                break;
        }
        this.description = "Produces " + efficiency + " per minute";
    }

    public int getEfficiency() {
        return efficiency;
    }
}
