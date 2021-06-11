package gs;

import gs.service.achievements.Achievement;
import gs.service.achievements.Achievements;
import gs.service.cases.Case;
import gs.service.cases.OptionCase;
import gs.service.cases.StringCase;
import gs.service.items.EnergySupply;
import gs.service.items.GraphicsCard;
import gs.service.items.Item;
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
    DataBase dataBase;

    public ConsoleListener(List<Player> active, Player player, TextChannel channel, DataBase dataBase) {
        this.active = active;
        this.player = player;
        this.channel = channel;
        consoleState = ConsoleState.HOME;
        this.dataBase = dataBase;
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

                // Getting item type
                List<Map.Entry<String, Integer>> list = new ArrayList<>(player.inventory.entrySet());

                String type;
                try {
                    type = list.get(itemNumber - 1).getKey();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    channel.sendMessage("Wrong number!");
                    return;
                }

                // Interpreting item effect
                switch (type) {
                    case "coffee":
                        player.updateEnergy(1);
                        player.statistics.updateCoffeeConsumed(1);
                        dataBase.updatePlayer(player);
                        channel.sendMessage(String.format(
                                "You have drank a cup of coffee! Energy: %d/%d",
                                player.getEnergy(),
                                player.getMaxEnergy()
                        ));
                        break;
                    case "energy_drink":
                        player.updateEnergy(2);
                        player.statistics.updateEnergyDrinksConsumed(1);
                        dataBase.updatePlayer(player);
                        channel.sendMessage(String.format(
                                "You have consumed a can of energy drink! Energy: %d/%d",
                                player.getEnergy(),
                                player.getMaxEnergy()
                        ));
                        break;
                    case "graphics_card_1":
                    case "graphics_card_2":
                    case "graphics_card_3":
                        List<Item> shop = shop();
                        GraphicsCard card = null;
                        for (Item product : shop) {
                            if (product.getType().equals(type)) {
                                card = (GraphicsCard) product;
                                break;
                            }
                        }
                        if (card == null) throw new IllegalStateException("Card is null");
                        boolean added = player.farm.addCard(card);
                        dataBase.updatePlayer(player);
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

                return;
            } else if (msg.equalsIgnoreCase("help")) {
                sendEnvironmentHelp("inventory-help.txt");
                return;
            }
        } else if (consoleState == ConsoleState.CASE) {
            if (msg.toLowerCase().contains("solve")) {
                if (player.getActiveCase() == null) {
                    channel.sendMessage("You have no cases opened. Type in \"case\" to get one.");
                    return;
                }

                if (player.getEnergy() <= 0) {
                    channel.sendMessage("Your energy is down. " +
                            "Drink some coffee or wait to restore it.");
                    return;
                }

                // Getting answer
                String content = msg
                        .toLowerCase()
                        .replace("solve", "")
                        .replace(" ", "");

                if (content.equals("")) {
                    sendEnvironmentHelp("case-help.txt");
                    return;
                }

                // Solving case
                boolean solved = false;
                Case activeCase = player.getActiveCase();
                if (activeCase.getClass() == OptionCase.class) {
                    int answer;
                    try {
                        answer = Integer.parseInt(content);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        channel.sendMessage("Wrong number format!");
                        return;
                    }

                    OptionCase optionCase = (OptionCase) activeCase;
                    solved = optionCase.solve(answer);

                } else if (activeCase.getClass() == StringCase.class) {
                    StringCase stringCase = (StringCase) activeCase;
                    solved = stringCase.solve(content);
                }

                player.updateEnergy(-1);

                MessageBuilder messageBuilder = new MessageBuilder();
                if (solved) {
                    player.statistics.updateCasesDone(1);
                    messageBuilder.append("Answer is correct! You have gained: " + activeCase.getProfit());
                } else {
                    messageBuilder.append("Answer is wrong. Try again or type in \"new\" to get another case");
                }
                dataBase.updatePlayer(player);

                messageBuilder
                        .appendNewLine()
                        .append("Energy left: " + player.getEnergy())
                        .send(channel);

                return;
            } else if (msg.equalsIgnoreCase("new")) {
                player.clearCase();
                drawCase();
                return;
            } else if (msg.equalsIgnoreCase("help")) {
                sendEnvironmentHelp("case-help.txt");
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
                    String type = item.getType();

                    if (player.inventory.containsKey(type)) {
                        player.inventory.replace(type, player.inventory.get(type) + 1);
                    } else {
                        player.inventory.put(type, 1);
                    }

                    player.updateMoney(-item.getPrice());
                    player.statistics.updatemoneySpent(item.getPrice());
                    dataBase.updatePlayer(player);
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
        } else if (consoleState == ConsoleState.FARM) {
            if (msg.toLowerCase().contains("uninstall")) {
                // Getting item number
                Integer itemNumber = getItemNumber(msg, "uninstall", "farm-help.txt");
                if (itemNumber == null) return;

                // Getting target GraphicsCard
                GraphicsCard card;
                try {
                    card = player.farm.cards.get(itemNumber - 1);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    channel.sendMessage("Wrong number!");
                    return;
                }

                // Uninstalling card
                player.farm.removeCard(card);
                if (player.inventory.containsKey(card.getType())) {
                    player.inventory.replace(card.getType(), player.inventory.get(card.getType()) + 1);
                } else {
                    player.inventory.put(card.getType(), 1);
                }
                channel.sendMessage("Graphics card was successfully uninstalled.");
                dataBase.updatePlayer(player);
                return;
            } else if (msg.equalsIgnoreCase("help")) {
                sendEnvironmentHelp("farm-help.txt");
                return;
            }
        }

        // SWITCH ENVIRONMENT COMMANDS, GLOBAL HELP, QUIT

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
        } else if (msg.equalsIgnoreCase("farm")) {
            consoleState = ConsoleState.FARM;
            drawFarm();
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
            sendEnvironmentHelp("CL-help.txt");
        } else {
            channel.sendMessage("Undefined command. For more information type in \"help\".");
        }
    }

    // ENVIRONMENT DRAWING

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
        Case aCase;

        // If a player has an active case, opens it
        // otherwise getting new one from the base
        if (player.getActiveCase() != null) {
            aCase = player.getActiveCase();
        } else {
            List<Case> cases = new ArrayList<>();

            // OPTION CASES

            Path optionCasesPath = Paths.get("src/main/java/gs/materials/option-cases.txt");

            List<String> optionCasesLines;
            try {
                optionCasesLines = Files.readAllLines(optionCasesPath);
            } catch (IOException e) {
                throw new IllegalStateException("No cases file", e);
            }

            for (String line : optionCasesLines) {
                String[] elements = line.split(";");
                String[] options = elements[3].split(",");
                cases.add(new OptionCase(
                        elements[0], // String name
                        elements[1], // String description
                        Integer.parseInt(elements[2]), // int profit
                        Arrays.asList(options), // List<String> options
                        Integer.parseInt(elements[4])) // int answer
                );
            }

            // STRING CASES

            Path stringCasesPath = Paths.get("src/main/java/gs/materials/string-cases.txt");

            List<String> stringCasesLines;
            try {
                stringCasesLines = Files.readAllLines(stringCasesPath);
            } catch (IOException e) {
                throw new IllegalStateException("No cases file", e);
            }

            for (String line : stringCasesLines) {
                String[] elements = line.split(";");
                cases.add(new StringCase(
                        elements[0], // String name
                        elements[1], // String description
                        Integer.parseInt(elements[2]), // int profit
                        elements[3].toLowerCase()) // String answer
                );
            }

            Random random = new Random();
            aCase = cases.get(random.nextInt(cases.size()));
            aCase.assignToPlayer(player);
        }

        // PRINTING CASE

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(aCase.getName())
                .setDescription(aCase.getDescription() + "\n" +
                        "Profit: " + aCase.getProfit())
                .setColor(Color.RED);

        Class<? extends Case> caseClass = aCase.getClass();
        if (caseClass == OptionCase.class) {
            OptionCase optionCase = (OptionCase) aCase;
            List<String> options = optionCase.getOptions();
            int optionNum = 1;
            for (String option : options) {
                embedBuilder.addField("Option " + optionNum++, option);
            }
            embedBuilder.setFooter("To send an answer, type in \"solve <option number>\"");
        } else if (caseClass == StringCase.class) {
            embedBuilder.setFooter("To send an answer, type in \"solve <your answer>\"");
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    private void drawInventory() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("INVENTORY")
                .setDescription("These are the things you own.")
                .setColor(Color.BLUE);

        List<Item> shop = shop();

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
            embedBuilder.setFooter("Wow, there's nothing...");
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    private void drawShop() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("SHOP")
                .setDescription("The place, where you can buy stuff.")
                .setColor(Color.GREEN);

        List<Item> shop = shop();
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

    private void drawFarm() {
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

    private void drawAchievements() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("ACHIEVEMENTS")
                .setDescription("All the goals you have reached.")
                .setColor(Color.YELLOW);

        int achievementCounter = 0;

        List<Achievement> achievements = Achievements.calculate(player);
        System.out.println();
        for (Achievement achievement : achievements) {

            if (achievement.getLevel() != null) {
                String achievementName = achievement.getName();
                switch (achievementName) {
                    case "Spender":
                        embedBuilder.addField(
                                achievementName + ", level " + achievement.getLevel(),
                                achievement.getDescription() + "\n" +
                                        "Money spent: " + player.statistics.getMoneySpent());
                        break;
                    case "Worker":
                        embedBuilder.addField(
                                achievementName + ", level " + achievement.getLevel(),
                                achievement.getDescription() + "\n" +
                                        "Cases done: " + player.statistics.getCasesDone());
                        break;
                    case "Coffee enjoyer":
                        embedBuilder.addField(
                                achievementName + ", level " + achievement.getLevel(),
                                achievement.getDescription() + "\n" +
                                        "Coffee consumed: " + player.statistics.getCoffeeConsumed());
                        break;
                    case "Energizer":
                        embedBuilder.addField(
                                achievementName + ", level " + achievement.getLevel(),
                                achievement.getDescription() + "\n" +
                                        "Energy drinks consumed: " + player.statistics.getCasesDone());
                        break;
                }
                achievementCounter++;
            }
        }

        if (achievementCounter == 0) {
            embedBuilder.setFooter("Not a single one yet :D");
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);
    }

    public void drawHomeScreen() {
        drawHome();
    }

    /**
     * Gets a number of selected item.
     *
     * @param msg      Original message from player
     * @param word     String that is deleted from file. Usually is the command text
     * @param helpFile A path to the help file, that fits the environment
     * @return Number of selected item
     */
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
                new EnergySupply("coffee", "Coffee", 50, 1, 1),
                new EnergySupply("energy_drink", "Energy drink", 80, 10, 2),
                new GraphicsCard("graphics_card_1", "GTX 680", 300, 2, 10),
                new GraphicsCard("graphics_card_2", "GTX 970", 1000, 10, 100),
                new GraphicsCard("graphics_card_3", "Titan Z", 5000, 30, 500)
        );
    }
}
