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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainChatListener implements MessageCreateListener {
    DiscordApi api;
    DataBase db;
    List<Player> online, offline;

    public MainChatListener(DiscordApi api) {
        this.api = api;

        try {
            db = new DataBase();
        } catch (SQLException e) {
            db = null;
            System.err.println("Database connection failed, working autonomously.");
        }

        online = new ArrayList<>();
        offline = new ArrayList<>();
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

            for (Player instance : online) {
                if (instance.getId() == author.getId()) {
                    event.getChannel().sendMessage("Console is already opened!");
                    return;
                }
            }

            Server server = event.getServer()
                    .orElseThrow(() -> new RuntimeException("Server is not present"));

            String discriminator = author.getDiscriminator()
                    .orElseThrow(() -> new RuntimeException("Discriminator is not present"));

            Player player = new Player(
                    author.getId(),
                    author.getDisplayName(),
                    discriminator,
                    db
            );

            if (db != null) {
                online.add(db.getPlayer(player));
            } else {
                boolean playerWasFound = false;

                for (Player instance : offline) {
                    if (player.equals(instance)) {
                        online.add(instance);
                        offline.remove(instance);
                        player = instance;
                        playerWasFound = true;
                        break;
                    }
                }

                if (!playerWasFound) {
                    online.add(player);
                }
            }

            player.startEnergyThread();
            player.farm.start();

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

            ConsoleListener console = new ConsoleListener(player, channel, db, online, offline);
            channel.addMessageCreateListener(console);
            console.drawHomeScreen();

            System.out.println("New player arrived. Active players now: " + online.toString());

        } else if (msg.equalsIgnoreCase("!clear")) {
            if (!event.getMessageAuthor().canManageRolesOnServer()) {
                event.getChannel().sendMessage("You don't have enough permissions " +
                        "to execute this command.");
                return;
            }

            Server server = event.getServer()
                    .orElseThrow(() -> new RuntimeException("Server is not present"));

            List<ServerTextChannel> channels = server.getTextChannels();

            for (ServerTextChannel channel : channels) {
                if (channel.getName().toLowerCase().contains("console")) {
                    channel.delete("Clear command");
                }
            }

            online.clear();
            offline.clear();

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
