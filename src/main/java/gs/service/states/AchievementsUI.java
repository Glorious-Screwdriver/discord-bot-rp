package gs.service.states;

import gs.DataBase;
import gs.service.Player;
import gs.service.achievements.Achievement;
import gs.service.achievements.Achievements;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.List;

public class AchievementsUI implements UI {
    @Override
    public void draw(TextChannel channel, Player player) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("ACHIEVEMENTS")
                .setDescription("All the goals you have reached.")
                .setColor(Color.YELLOW);

        int achievementCounter = 0;

        List<Achievement> achievements = Achievements.calculate(player);
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

    @Override
    public boolean readCommand(MessageCreateEvent event, Player player, DataBase db) {
        return false;
    }
}
