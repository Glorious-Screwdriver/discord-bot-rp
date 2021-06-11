package gs;

import gs.service.Player;
import org.javacord.api.DiscordApi;
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

public class MainChatListener implements MessageCreateListener {
    DiscordApi api;
    List<Player> active;
    DataBase dataBase;

    public MainChatListener(DiscordApi api, List<Player> active) {
        dataBase = new DataBase();
        this.api = api;
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
                if (player.getId() == author.getId()) {
                    event.getChannel().sendMessage("Console is already opened!");
                    return;
                }
            }

            Server server = event.getServer()
                    .orElseThrow(() -> new RuntimeException("Server is not present"));

            String discriminator = author.getDiscriminator()
                    .orElseThrow(() -> new RuntimeException("Discriminator is not present"));

            ServerTextChannel channel = new ServerTextChannelBuilder(server)
                    .setName("console-" + discriminator)
                    .addPermissionOverwrite(server.getEveryoneRole(),
                            new PermissionsBuilder()
                                    .setState(PermissionType.READ_MESSAGES, PermissionState.DENIED)
                                    .build()
                    )
                    .addPermissionOverwrite(
                            author.asUser()
                                    .orElseThrow(() -> new RuntimeException("User is not present")),
                            new PermissionsBuilder()
                                    .setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED)
                                    .build())
                    .addPermissionOverwrite(
                            api.getYourself(),
                            new PermissionsBuilder()
                                    .setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED)
                                    .build())
                    .create()
                    .join();

            Player player = new Player(
                    author.getId(),
                    author.getDisplayName(),
                    discriminator
            );
            active.add(dataBase.getPlayer(player));
            System.out.println("New player arrived. Active players now: " + active.toString());

            ConsoleListener console = new ConsoleListener(active, player, channel, dataBase);
            channel.addMessageCreateListener(console);
            console.drawHomeScreen();

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

        throw new IllegalStateException("No MCL-help file");
    }
}
