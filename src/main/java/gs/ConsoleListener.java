package gs;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConsoleListener implements MessageCreateListener {
    List<Player> active;
    Player player;
    TextChannel channel;

    public ConsoleListener(List<Player> active, Player player) {
        this.active = active;
        this.player = player;
        this.channel = player.console.getChannel();
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (msg.equalsIgnoreCase("quit")) {
            channel.sendMessage("Closing console...");
            channel.asServerChannel()
                    .orElseThrow(() -> new RuntimeException("Server channel is not present"))
                    .delete("Close console command");
            active.remove(player);
            System.out.println("Player disconnected. Active players now: " + active.toString());
        } else if (msg.equalsIgnoreCase("help")) {
            channel.sendMessage(getHelpString());
        } else {
            channel.sendMessage("Undefined command. For more information type in \"help\".");
        }
    }

    private String getHelpString() {
        Path helpFilePath = Paths.get("src/main/java/gs/materials/CCL-help.txt");

        try {
            return String.join("\n", Files.readAllLines(helpFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("No MCP-help file");
    }
}
