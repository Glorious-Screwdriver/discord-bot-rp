package gs;

import org.javacord.api.entity.Permissionable;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MainChatListener implements MessageCreateListener {
    List<Player> active;

    public MainChatListener(List<Player> active) {
        this.active = active;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (msg.equalsIgnoreCase("!help")) {
            new MessageBuilder()
                    .append(getHelpString())
                    .send(event.getChannel());
        } else if (msg.equalsIgnoreCase("!console")) {
            MessageAuthor author = event.getMessageAuthor();

            for (Player player : active) {
                if (player.id == author.getId()) {
                    event.getChannel().sendMessage("Console is already opened!");
                    return;
                }
            }

            Server server = event.getServer()
                    .orElseThrow(() -> new RuntimeException("Server is not present"));

            String discriminator = author.getDiscriminator()
                    .orElseThrow(() -> new RuntimeException("Discriminator is not present"));

            ServerTextChannel console = new ServerTextChannelBuilder(server)
                    .setName("Console-" + discriminator)
                    .create()
                    .join();

            Player player = new Player(
                    author.getId(),
                    author.getDisplayName(),
                    discriminator,
                    new Console(console)
            );
            active.add(player);
            System.out.println("New player arrived. Active players now: " + active.toString());

            console.addMessageCreateListener(new ConsoleListener(active, player));
            console.sendMessage("Console opened, sir.");

        } else if (msg.equalsIgnoreCase("!clear")) {
            Server server = event.getServer()
                    .orElseThrow(() -> new RuntimeException("Server is not present"));

            List<ServerTextChannel> channels = server.getTextChannels();

            for (ServerTextChannel channel : channels) {
                if (channel.getName().toLowerCase().contains("console")) {
                    channel.delete("Clear command");
                }
            }

            active.clear();
            System.out.println("All players and consoles deleted.");
        }
    }

    private String getHelpString() {
        Path helpFilePath = Paths.get("src/main/java/gs/materials/MCL-help.txt");

        try {
            return String.join("\n", Files.readAllLines(helpFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("No MCP-help file");
    }
}
