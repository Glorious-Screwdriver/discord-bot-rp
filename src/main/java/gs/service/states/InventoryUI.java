package gs.service.states;

import gs.DataBase;
import gs.service.Player;
import gs.service.items.GraphicsCard;
import gs.service.items.Item;
import gs.util.Helper;
import gs.util.Shop;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryUI implements UI {
    /**
     * Shows player's items
     * @param channel Target text channel
     * @param player Target player
     */
    @Override
    public void draw(TextChannel channel, Player player) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("INVENTORY")
                .setDescription("These are the things you own.")
                .setColor(Color.BLUE);

        List<Item> shop = Shop.getItemList();

        final int[] itemCounter = {1};
        if (player.inventory.size() > 0) {
            player.inventory.forEach((key, value) -> {
                String name = null;
                for (Item item : shop) {
                    if (item.getType().equals(key)) {
                        name = item.getName();
                        break;
                    }
                }
                if (name == null) throw new IllegalStateException("Name is null");
                embedBuilder.addField(itemCounter[0]++ + ". " + name, "Quantity: " + value);
            });
        } else {
            embedBuilder.setFooter("Wow, such emptiness...");
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    @Override
    public boolean readCommand(MessageCreateEvent event, Player player, DataBase db) {
        String msg = event.getMessageContent().toLowerCase();
        TextChannel channel = event.getChannel();

        if (msg.contains("use")) {
            // Getting item number

            String content = msg.replace("use ", "");

            int itemNumber;
            try {
                itemNumber = Integer.parseInt(content);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Helper.sendInventoryHelp(channel);
                return true;
            }

            // Getting item type
            List<String> keyList = new ArrayList<>(player.inventory.keySet());
            String type;
            try {
                type = keyList.get(itemNumber - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                channel.sendMessage("Wrong number!");
                return true;
            }

            // Applying item effect
            switch (type) {
                case "coffee":
                    player.updateEnergy(1);
                    player.statistics.updateCoffeeConsumed(1);

                    if (db != null) {
                        db.updatePlayer(player);
                    }

                    channel.sendMessage(String.format(
                            "You have drank a cup of coffee! Energy: %d/%d",
                            player.getEnergy(),
                            player.getMaxEnergy()
                    ));
                    break;
                case "energy_drink":
                    player.updateEnergy(2);
                    player.statistics.updateEnergyDrinksConsumed(1);

                    if (db != null) {
                        db.updatePlayer(player);
                    }

                    channel.sendMessage(String.format(
                            "You have consumed a can of energy drink! Energy: %d/%d",
                            player.getEnergy(),
                            player.getMaxEnergy()
                    ));
                    break;
                case "graphics_card_1":
                case "graphics_card_2":
                case "graphics_card_3":
                    List<Item> shop = Shop.getItemList();
                    GraphicsCard card = null;
                    for (Item product : shop) {
                        if (product.getType().equals(type)) {
                            card = (GraphicsCard) product;
                            break;
                        }
                    }
                    if (card == null) throw new IllegalStateException("Card is null");
                    boolean added = player.farm.addCard(card);

                    if (db != null) {
                        db.updatePlayer(player);
                    }

                    if (added) {
                        channel.sendMessage(String.format(
                                "You have installed %s in your mining farm.",
                                card.getName()
                        ));
                    } else {
                        channel.sendMessage("You have reached the limit of graphics cards in you farm!");
                    }
                    break;
            }

            // Deleting item from inventory
            if (player.inventory.get(type) == 1) {
                player.inventory.remove(type);
            } else {
                player.inventory.replace(type, player.inventory.get(type) - 1);
            }

            return true;
        } else if (msg.replace(" ", "").equals("help")) {
            Helper.sendInventoryHelp(channel);
            return true;
        } else {
            return false;
        }
    }
}
