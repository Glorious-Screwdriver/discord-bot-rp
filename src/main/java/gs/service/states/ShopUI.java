package gs.service.states;

import gs.DataBase;
import gs.service.Player;
import gs.service.items.EnergySupply;
import gs.service.items.GraphicsCard;
import gs.service.items.Item;
import gs.util.Helper;
import gs.util.Shop;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.List;

public class ShopUI implements UI {
    /**
     * Shows all purchasable items
     * @param channel Target channel
     * @param player Target player
     */
    @Override
    public void draw(TextChannel channel, Player player) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("SHOP")
                .setDescription("The place, where you can buy stuff.")
                .setColor(Color.GREEN);

        List<Item> shop = Shop.getItemList();
        int itemCounter = 1;

        for (Item product : shop) {
            if (product.getClass() == EnergySupply.class) {
                EnergySupply energySupply = (EnergySupply) product;
                embedBuilder.addField(
                        itemCounter++ + ". " + energySupply.getName(),
                        energySupply.getDescription() + "\n" +
                                "Price: " + energySupply.getPrice() + "\n" +
                                "Required level: " + energySupply.getRequiredLevel() + "\n" +
                                "Energy: " + energySupply.getProvidedEnergy()
                );
            } else if (product.getClass() == GraphicsCard.class) {
                GraphicsCard card = (GraphicsCard) product;
                embedBuilder.addField(
                        itemCounter++ + ". " + card.getName(),
                        card.getDescription() + "\n" +
                                "Price: " + card.getPrice() + "\n" +
                                "Required level: " + card.getRequiredLevel() + "\n" +
                                "Efficiency: " + card.getEfficiency()
                );
            }
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    @Override
    public boolean readCommand(MessageCreateEvent event, Player player, DataBase db) {
        String msg = event.getMessageContent().toLowerCase();
        TextChannel channel = event.getChannel();

        if (msg.contains("buy")) {
            // Getting item number

            String content = msg.replace("buy ", "");

            int itemNumber;
            try {
                itemNumber = Integer.parseInt(content);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Helper.sendShopHelp(channel);
                return true;
            }

            // Getting target Item

            List<Item> shop = Shop.getItemList();
            Item item;
            try {
                item = shop.get(itemNumber - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                channel.sendMessage("Wrong number!");
                return true;
            }

            // Purchasing

            if (player.getMoney() >= item.getPrice() && player.getLevel() >= item.getRequiredLevel()) {
                String type = item.getType();

                if (player.inventory.containsKey(type)) {
                    player.inventory.replace(type, player.inventory.get(type) + 1);
                } else {
                    player.inventory.put(type, 1);
                }

                player.updateMoney(-item.getPrice());
                player.statistics.updatemoneySpent(item.getPrice());

                if (db != null) {
                    db.updatePlayer(player);
                }

                channel.sendMessage(String.format(
                        "You have purchased %s!\nMoney left: %d",
                        item.getName(),
                        player.getMoney()
                ));
            } else {
                if (player.getMoney() < item.getPrice()) {
                    channel.sendMessage("Oops! Looks like you don't have enough money ;)");
                } else {
                    channel.sendMessage("Your level is not high enough to buy this item.");
                }
            }

            return true;
        } else if (msg.replace(" ", "").equals("help")) {
            Helper.sendShopHelp(channel);
            return true;
        } else {
            return false;
        }
    }
}
