package gs;

import gs.service.EnergySupply;
import gs.service.Item;
import gs.service.Player;
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
        if (event.getMessageAuthor().isYourself()) return;

        // CONSOLE STATE CHECK
        if (consoleState == ConsoleState.INVENTORY) {
            if (msg.toLowerCase().contains("use")) {
                // Getting item number
                Integer itemNumber = getItemNumber(msg, "use", "inventory-help.txt");
                if (itemNumber == null) return;

                // Getting item name
                List<Map.Entry<String, Integer>> list = new ArrayList<>(player.inventory.entrySet());

                String item;
                try {
                    item = list.get(itemNumber - 1).getKey();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    channel.sendMessage("Wrong number!");
                    return;
                }

                // Interpreting item effect
                if (item.equals("Coffee")) {
                    player.updateEnergy(1);
                    channel.sendMessage(String.format(
                            "You have drank a cup of coffee! Energy: %d/%d",
                            player.getEnergy(),
                            player.getMaxEnergy()
                    ));
                } else if (item.equals("Energy drink")) {
                    player.updateEnergy(2);
                    channel.sendMessage(String.format(
                            "You have consumed a can of energy drink! Energy: %d/%d",
                            player.getEnergy(),
                            player.getMaxEnergy()
                    ));
                }

                // Deleting item from inventory
                if (player.inventory.get(item) == 1) {
                    player.inventory.remove(item);
                } else {
                    player.inventory.replace(item, player.inventory.get(item) - 1);
                }

                return;
            } else if (msg.equalsIgnoreCase("help")) {
                sendEnvironmentHelp("inventory-help.txt");
                return;
            }

        } else if (consoleState == ConsoleState.SHOP) {
            if (msg.toLowerCase().contains("buy")) {
                // Getting item number
                Integer itemNumber = getItemNumber(msg, "buy", "shop-help.txt");
                if (itemNumber == null) return;

                // Getting target Item
                List<Item> shop = shop();
                Item item;
                try {
                    item = shop.get(itemNumber - 1);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    channel.sendMessage("Wrong number!");
                    return;
                }

                // Purchasing
                if (player.getMoney() >= item.getPrice() && player.getLevel() >= item.getRequiredLevel()) {
                    String name = item.getName();

                    if (player.inventory.containsKey(name)) {
                        player.inventory.replace(name, player.inventory.get(name) + 1);
                    } else {
                        player.inventory.put(name, 1);
                    }

                    player.updateMoney(-item.getPrice());
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
                    return;
                }

                return;
            } else if (msg.equalsIgnoreCase("help")) {
                sendEnvironmentHelp("shop-help.txt");
                return;
            }
        }

        // SWITCH ENVIRONMENT COMMANDS, HELP, QUIT
        if (msg.equalsIgnoreCase("home")) {
            consoleState = ConsoleState.HOME;
            drawHome();
        } else if (msg.equalsIgnoreCase("case")) {
//            consoleState = ConsoleState.CASE;
            drawCase();
        } else if (msg.equalsIgnoreCase("inventory")) {
            consoleState = ConsoleState.INVENTORY;
            drawInventory();
        } else if (msg.equalsIgnoreCase("shop")) {
            consoleState = ConsoleState.SHOP;
            drawShop();
        } else if (msg.equalsIgnoreCase("achievements")) {
//            consoleState = ConsoleState.ACHIEVEMENTS;
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

        player.inventory.forEach((key, value) -> embedBuilder.addField(key, String.valueOf(value)));

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    private void drawShop() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("SHOP")
                .setDescription("The place, where you can buy stuff.")
                .setColor(Color.GREEN);

        List<Item> shop = shop();

        for (Item product : shop) {
            if (product.getClass() == EnergySupply.class) {
                EnergySupply energySupply = (EnergySupply) product;
                embedBuilder.addField(
                        energySupply.getName(),
                        energySupply.getDescription() + "\n" +
                                "Price: " + energySupply.getPrice() + "\n" +
                                "Required level: " + energySupply.getRequiredLevel() + "\n" +
                                "Energy: " + energySupply.getProvidedEnergy()
                );
            }
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    private void drawAchievements() {
        channel.sendMessage("ACHIEVEMENTS environment is not supported yet!");
    }

    public void drawHomeScreen() {
        drawHome();
    }

    private Integer getItemNumber(String msg, String word, String helpFile) {
        String content = msg
                .replace(word, "")
                .replace(" ", "");

        int itemNumber;
        try {
            itemNumber = Integer.parseInt(content);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendEnvironmentHelp(helpFile);
            return null;
        }
        return itemNumber;
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
            throw new IllegalStateException("No help file", e);
        }
    }

    private List<Item> shop() {
        return Arrays.asList(
                new EnergySupply("Coffee", 50, 1, 1),
                new EnergySupply("Energy drink", 80, 2, 2)
                //TODO add graphics cards
        );
    }
}
