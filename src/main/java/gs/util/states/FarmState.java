package gs.util.states;

import gs.DataBase;
import gs.service.Player;
import gs.service.items.GraphicsCard;
import gs.util.Helper;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class FarmState implements State {
    @Override
    public void draw(TextChannel channel, Player player) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("FARM")
                .setDescription("The place where your graphics cards work.")
                .setColor(Color.CYAN);

        int itemCounter = 1;
        for (GraphicsCard card : player.farm.cards) {
            embedBuilder.addField(
                    itemCounter++ + ". " + card.getName(),
                    "Efficiency: " + card.getEfficiency());
        }
        embedBuilder.setFooter("Average income per minute: " + player.farm.getIncome() + "\n" +
                "Slots used: " + player.farm.cards.size() + "/" + player.farm.getLimit());

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    @Override
    public boolean readCommand(MessageCreateEvent event, Player player, DataBase db) {
        String msg = event.getMessageContent().toLowerCase();
        TextChannel channel = event.getChannel();

        if (msg.contains("uninstall")) {
            // Getting item number

            String content = msg.replace("uninstall ", "");

            int itemNumber;
            try {
                itemNumber = Integer.parseInt(content);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Helper.sendFarmHelp(channel);
                return true;
            }

            // Getting target GraphicsCard

            GraphicsCard card;
            try {
                card = player.farm.cards.get(itemNumber - 1);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                channel.sendMessage("Wrong number!");
                return true;
            }

            // Uninstalling card

            player.farm.removeCard(card);
            if (player.inventory.containsKey(card.getType())) {
                player.inventory.replace(card.getType(), player.inventory.get(card.getType()) + 1);
            } else {
                player.inventory.put(card.getType(), 1);
            }
            channel.sendMessage("Graphics card was successfully uninstalled.");

            if (db != null) {
                db.updatePlayer(player);
            }

            return true;
        } else if (msg.replace(" ", "").equals("help")) {
            Helper.sendFarmHelp(channel);
            return true;
        } else {
            return false;
        }
    }
}
