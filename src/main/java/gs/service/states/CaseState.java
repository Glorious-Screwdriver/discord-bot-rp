package gs.service.states;

import gs.DataBase;
import gs.service.Player;
import gs.service.cases.Case;
import gs.service.cases.OptionCase;
import gs.service.cases.StringCase;
import gs.util.Helper;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CaseState implements State {
    /**
     * If a player has an active case, opens it. Otherwise gets a new one from the base
     * @param channel Target text channel
     * @param player Target player
     */
    @Override
    public void draw(TextChannel channel, Player player) {
        Case aCase;

        if (player.getActiveCase() != null) {
            aCase = player.getActiveCase();
        } else {
            List<Case> cases = new ArrayList<>();

            // Option cases initialisation

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

            // String cases initialisation

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

            // Assigning case to the player

            Random random = new Random();
            aCase = cases.get(random.nextInt(cases.size()));
            aCase.assignToPlayer(player);
        }

        // Printing case

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

    @Override
    public boolean readCommand(MessageCreateEvent event, Player player, DataBase db) {
        String msg = event.getMessageContent().toLowerCase();
        TextChannel channel = event.getChannel();

        if (msg.contains("solve")) {
            if (player.getActiveCase() == null) {
                channel.sendMessage("You have no cases opened. Type in \"case\" to get one.");
                return true;
            }

            if (player.getEnergy() <= 0) {
                channel.sendMessage("Your energy is down. " +
                        "Drink some coffee or wait to restore it.");
                return true;
            }

            // Getting answer

            String content = msg.replace("solve ", "");

            if (content.equals("")) {
                Helper.sendCaseHelp(channel);
                return true;
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
                    return true;
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

            if (db != null) {
                db.updatePlayer(player);
            }

            messageBuilder
                    .appendNewLine()
                    .append("Energy left: " + player.getEnergy())
                    .send(channel);

            return true;
        } else if (msg.equals("new")) {
            player.clearCase();
            draw(channel, player);
            return true;
        } else if (msg.replace(" ", "").equals("help")) {
            Helper.sendCaseHelp(channel);
            return true;
        } else {
            return false;
        }
    }
}
