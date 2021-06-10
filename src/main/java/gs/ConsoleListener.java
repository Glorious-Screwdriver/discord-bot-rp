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

        // CONSOLE STATE CHECK
        if (consoleState == ConsoleState.INVENTORY) {
            if (!event.getMessageAuthor().isYourself())
                if (msg.toLowerCase().contains("use")) {
                    String content = msg
                            .replace("use", "")
                            .replace(" ", "");

                    int itemNumber;
                    try {
                        itemNumber = Integer.parseInt(content);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        sendEnvironmentHelp("inventory-help.txt");
                        return;
                    }

                    ArrayList<Item> items = new ArrayList<>();
                    for (Map.Entry<Item, Integer> entry : player.inventory.entrySet()) {
                        items.add(entry.getKey());
                    }

                    Item item = items.get(itemNumber - 1);

                    // ENERGY SUPPLY
                    if (item.getClass() == EnergySupply.class) {
                        EnergySupply energySupply = (EnergySupply) item;
                        player.updateEnergy(energySupply.getProvidedEnergy());
                        channel.sendMessage(String.format("Energy restored! You now have %d/%d",
                                player.getEnergy(),
                                player.getMaxEnergy()
                        ));
                    }
                    // GRAPHICS CARD
                    else if (item.getClass() == GraphicsCard.class) {
                        channel.sendMessage("Using graphics cards is not supported yet!");
                        //TODO "using" graphics cards
                    }

                    return;
                } else if (msg.equalsIgnoreCase("help")) {
                    sendEnvironmentHelp("inventory-help.txt");
                    return;
                }
        } else if (consoleState == ConsoleState.SHOP) {
            if (!event.getMessageAuthor().isYourself()) {
                if (msg.toLowerCase().contains("buy")) {
                    String content = msg
                            .replace("buy", "")
                            .replace(" ", "");

                    int itemNumber;
                    try {
                        itemNumber = Integer.parseInt(content);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        sendEnvironmentHelp("shop-help.txt");
                        return;
                    }

                    List<Item> products = shop();

                    Item item = products.get(itemNumber - 1);

                    if (player.getMoney() >= item.getPrice() && player.getLevel() >= item.getRequiredLevel()) {
                        player.updateMoney(-item.getPrice());

                        if (player.inventory.containsKey(item)) {
                            player.inventory.replace(item, player.inventory.get(item) + 1);
                        } else {
                            player.inventory.put(item, 1);
                        }

                        System.out.println(player.inventory.toString());

                        channel.sendMessage(String.format("You have purchased %s!\nMoney left: %d",
                                item.getName(),
                                player.getMoney()
                        ));
                    } else {
                        if (player.getMoney() < item.getPrice()) {
                            channel.sendMessage("Oops! Looks like you don't have enough money ;)");
                        } else {
                            channel.sendMessage("Your level not high enough, to buy this item.");
                        }
                        return;
                    }

                    return;
                } else if (msg.equalsIgnoreCase("help")) {
                    sendEnvironmentHelp("shop-help.txt");
                    return;
                }
            }
        }

        // SWITCH ENVIRONMENT COMMANDS + HELP, QUIT
        if (msg.equalsIgnoreCase("home")) {
            consoleState = ConsoleState.HOME;
            drawHome();
        } else if (msg.equalsIgnoreCase("case")) {
            consoleState = ConsoleState.CASE;
            drawCase();
        } else if (msg.equalsIgnoreCase("inventory")) {
            consoleState = ConsoleState.INVENTORY;
            drawInventory();
        } else if (msg.equalsIgnoreCase("shop")) {
            consoleState = ConsoleState.SHOP;
            drawShop();
        } else if (msg.equalsIgnoreCase("achievements")) {
            consoleState = ConsoleState.ACHIEVEMENTS;
            drawAchievements();
        } else if (msg.equalsIgnoreCase("quit")) {
            channel.sendMessage("Closing console...");
            channel.asServerChannel()
                    .orElseThrow(() -> new RuntimeException("Server channel is not present"))
                    .delete("Close console command");
            active.remove(player);
            System.out.println("Player disconnected. Active players now: " + active.toString());
        } else if (msg.equalsIgnoreCase("help")) {
            sendHomeHelp();
        } else {
            if (!event.getMessageAuthor().isYourself())
                channel.sendMessage("Undefined command. For more information type in \"help\".");
        }
    }

    private void drawHome() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("HOME")
                        .setDescription(String.format("Hello, %s!", player.getDisplayName()))
                        .addField("Level", String.valueOf(player.getLevel()))
                        .addField("Money", String.valueOf(player.getMoney()))
                        .addField("Energy", player.getEnergy() + "/" + player.getMaxEnergy())
                        .setColor(Color.ORANGE)
                ).send(channel);
    }

    private void drawCase() {
        channel.sendMessage("CASE environment is not supported yet!");
    }

    private void drawInventory() {
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
    }

    private void drawShop() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("SHOP")
                .setDescription("The place, where you can buy stuff.")
                .setColor(Color.GREEN);

        List<Item> products = shop();

        for (Item product : products) {
            embedBuilder.addField(product.getName(), product.getDescription());
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    private void drawAchievements() {
        channel.sendMessage("ACHIEVEMENTS environment is not supported yet!");
    }

    public void drawHomeScreen() {
        drawHome();
    }

    private void sendHomeHelp() {
        channel.sendMessage(getHelpString("CL-help.txt"));
    }

    private void sendEnvironmentHelp(String file) {
        new MessageBuilder()
                .append(getHelpString(file)).appendNewLine()
                .appendNewLine()
                .append(getHelpString("CL-help.txt"))
                .send(channel);
    }

    private String getHelpString(String file) {
        Path helpFilePath = Paths.get("src/main/java/gs/materials/" + file);

        try {
            return String.join("\n", Files.readAllLines(helpFilePath));
        } catch (IOException e) {
            throw new IllegalStateException("No CL-help file", e);
        }
    }

    private List<Item> shop() {
        return Arrays.asList(
                new EnergySupply("Coffee", 1, 50, 1),
                new EnergySupply("Energy drink", 2, 80, 3)
                //TODO add graphics cards
        );
    }
}
