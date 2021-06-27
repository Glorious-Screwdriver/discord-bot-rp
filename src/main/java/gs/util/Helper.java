package gs.util;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Helper {
    public static void sendHomeHelp(TextChannel channel) {
        new MessageBuilder()
                .append(getHelpString("CL-help.txt"))
                .send(channel);
    }

    public static void sendInventoryHelp(TextChannel channel) {
        sendHelp("inventory-help.txt", channel);
    }

    public static void sendCaseHelp(TextChannel channel) {
        sendHelp("case-help.txt", channel);
    }

    public static void sendShopHelp(TextChannel channel) {
        sendHelp("shop-help.txt", channel);
    }

    public static void sendFarmHelp(TextChannel channel) {
        sendHelp("farm-help.txt", channel);
    }

    private static void sendHelp(String file, TextChannel channel) {
        new MessageBuilder()
                .append(getHelpString(file))
                .appendNewLine()
                .appendNewLine()
                .append(getHelpString("CL-help.txt"))
                .send(channel);
    }

    private static String getHelpString(String file) {
        Path helpFilePath = Paths.get("src/main/java/gs/materials/" + file);

        try {
            return String.join("\n", Files.readAllLines(helpFilePath));
        } catch (IOException e) {
            throw new IllegalStateException("No help file", e);
        }
    }
}
