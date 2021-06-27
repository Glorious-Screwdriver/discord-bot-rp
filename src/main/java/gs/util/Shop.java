package gs.util;

import gs.service.items.EnergySupply;
import gs.service.items.GraphicsCard;
import gs.service.items.Item;

import java.util.Arrays;
import java.util.List;

public class Shop {
    public static List<Item> getItemList() {
        return Arrays.asList(
                new EnergySupply("coffee", "Coffee", 50, 1, 1),
                new EnergySupply("energy_drink", "Energy drink", 80, 10, 2),
                new GraphicsCard("graphics_card_1", "GTX 680", 300, 2, 10),
                new GraphicsCard("graphics_card_2", "GTX 970", 1000, 10, 100),
                new GraphicsCard("graphics_card_3", "Titan Z", 5000, 30, 500)
        );
    }
}
