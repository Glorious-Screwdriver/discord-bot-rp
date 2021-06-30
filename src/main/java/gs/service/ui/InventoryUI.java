package gs.service.ui;

import gs.DataBase;
import gs.service.Player;
import gs.service.items.Item;
import gs.util.Helper;
import gs.util.Items;
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

        final int[] itemCounter = {1};
        if (player.inventory.size() > 0) {
            player.inventory.forEach((key, value) -> {
                String name = Items.get(key).getName();
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

            // Getting item object
            List<String> keyList = new ArrayList<>(player.inventory.keySet());
            String type;
            try {
                type = keyList.get(itemNumber - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                channel.sendMessage("Wrong number!");
                return true;
            }

            Item item = Items.get(type);

            // Applying item effect
            String response = item.use(player);
            channel.sendMessage(response);

            if (db != null) {
                db.updatePlayer(player);
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
