package gs;

import gs.util.ConsoleState;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class ConsoleListener implements MessageCreateListener {
    List<Player> active;
    Player player;
    TextChannel channel;
    ConsoleState consoleState;

    public ConsoleListener(List<Player> active, Player player, TextChannel channel) {
        this.active = active;
        this.player = player;
        this.channel = channel;
        consoleState = ConsoleState.HOME;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (consoleState == ConsoleState.INVENTORY) {
            if (msg.contains("use")) {
                String content = msg.replace("use", "").replace(" ", "");
                int itemNumber;

                try {
                    itemNumber = Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    channel.sendMessage("Wrong number format!");
                    return;
                }

                ArrayList<Item> items = new ArrayList<>();
                for (Map.Entry<Item, Integer> entry : player.inventory.entrySet()) {
                    items.add(entry.getKey());
                }

                Item item = items.get(itemNumber - 1);

                // ENERGY SUPPLY
                if (item.getType() == 0) {
                    EnergySupply energySupply = (EnergySupply) item;
                    player.updateEnergy(energySupply.getProvidedEnergy());
                    channel.sendMessage(String.format("Energy restored! You now have %d/%d",
                            player.getEnergy(),
                            player.getMaxEnergy()
                    ));
                }

                return;
            }
        }

        if (msg.equalsIgnoreCase("home")) {
            draw(consoleState = ConsoleState.HOME);
        } else if (msg.equalsIgnoreCase("case")) {
            draw(consoleState = ConsoleState.CASE);
        } else if (msg.equalsIgnoreCase("inventory")) {
            draw(consoleState = ConsoleState.INVENTORY);
        } else if (msg.equalsIgnoreCase("shop")) {
            draw(consoleState = ConsoleState.SHOP);
        } else if (msg.equalsIgnoreCase("achievements")) {
            draw(consoleState = ConsoleState.ACHIEVEMENTS);
        } else if (msg.equalsIgnoreCase("quit")) {
            channel.sendMessage("Closing console...");
            channel.asServerChannel()
                    .orElseThrow(() -> new RuntimeException("Server channel is not present"))
                    .delete("Close console command");
            active.remove(player);
            System.out.println("Player disconnected. Active players now: " + active.toString());
        } else if (msg.equalsIgnoreCase("help")) {
            channel.sendMessage(getHelpString());
        } else {
            if (!event.getMessageAuthor().isYourself())
                channel.sendMessage("Undefined command. For more information type in \"help\".");
        }
    }

    private String getHelpString() {
        Path helpFilePath = Paths.get("src/main/java/gs/materials/CL-help.txt");

        try {
            return String.join("\n", Files.readAllLines(helpFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("No CL-help file");
    }

    private void draw(ConsoleState consoleState) {
        if (consoleState == ConsoleState.HOME) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle("HOME")
                            .setDescription(String.format("Hello, %s!", player.getDisplayName()))
                            .addField("Level", String.valueOf(player.getLevel()))
                            .addField("Money", String.valueOf(player.getMoney()))
                            .addField("Energy", player.getEnergy() + "/" + player.getMaxEnergy())
                            .setColor(Color.ORANGE)
                    ).send(channel);
        } else if (consoleState == ConsoleState.CASE) {
            channel.sendMessage("CASE environment is not supported yet!");
        } else if (consoleState == ConsoleState.INVENTORY) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("INVENTORY")
                    .setDescription("These are the things you own.")
                    .setColor(Color.BLUE);

            for (Map.Entry<Item, Integer> entry : player.inventory.entrySet()) {
                embedBuilder.addField(
                        entry.getKey().getName(),
                        String.valueOf(entry.getValue())
                );
            }

            new MessageBuilder().setEmbed(embedBuilder).send(channel);
        } else if (consoleState == ConsoleState.SHOP) {
            channel.sendMessage("SHOP environment is not supported yet!");
        } else if (consoleState == ConsoleState.ACHIEVEMENTS) {
            channel.sendMessage("ACHIEVEMENTS environment is not supported yet!");
        }
    }

    public void drawHomeScreen() {
        draw(ConsoleState.HOME);
    }
}
