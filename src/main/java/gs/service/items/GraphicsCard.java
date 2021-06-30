package gs.service.items;

import gs.service.Player;
import gs.util.Items;

import java.util.List;

public class GraphicsCard extends Item {
    private final int efficiency;

    public GraphicsCard(String type, String name, int price, int requiredLevel, int efficiency) {
        super(type, name, "Produces " + efficiency + " per minute", price, requiredLevel);
        this.efficiency = efficiency;
    }

    // Перегрузка конструктора для работы с базой данных
    // Это какой-то ужас, надо переписывать
    public GraphicsCard(String type) {
        super("", "", "", 0, 0);

        GraphicsCard card = (GraphicsCard) Items.get(type);
        this.type = card.getType();
        this.name = card.getName();
        this.description = card.getDescription();
        this.price = card.getPrice();
        this.requiredLevel = card.getRequiredLevel();
        this.efficiency = card.getEfficiency();
    }

    @Override
    public String use(Player player) {
        GraphicsCard card = (GraphicsCard) Items.get(type);
        boolean added = player.farm.addCard(card);

        if (added) {
            return String.format(
                    "You have installed %s in your mining farm.",
                    card.getName()
            );
        } else {
            return "You have reached the limit of graphics cards in you farm!";
        }
    }

    public int getEfficiency() {
        return efficiency;
    }
}
