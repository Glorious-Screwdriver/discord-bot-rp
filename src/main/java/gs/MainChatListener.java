package gs;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.List;
import java.util.Optional;

public class MainChatListener implements MessageCreateListener {
//    Path helpFilePath = Paths.get("src/main/java/gs/materials/MCL-help.txt");
    List<Player> active;

    public MainChatListener(List<Player> active) {
        this.active = active;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (msg.equalsIgnoreCase("!help")) {
            new MessageBuilder()
                    .append("!console - open console\n")
                    .append("!clear - close all opened consoles and delete all players from active")
                    .send(event.getChannel());

        } else if (msg.equalsIgnoreCase("!console")) {
            MessageAuthor author = event.getMessageAuthor();

            for (Player player : active) {
                if (player.id == author.getId()) {
                    event.getChannel().sendMessage("Console is already opened!");
                    return;
                }
            }

            Optional<Server> optionalServer = event.getServer();
            if (!optionalServer.isPresent()) throw new RuntimeException("Server is not present");
            Server server = optionalServer.get();

            Optional<String> optionalDiscriminator = author.getDiscriminator();
            if (!optionalDiscriminator.isPresent()) throw new RuntimeException("Discriminator is not present");
            String discriminator = optionalDiscriminator.get();

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
            Optional<Server> optionalServer = event.getServer();
            if (!optionalServer.isPresent()) throw new RuntimeException("Server is not present");
            Server server = optionalServer.get();

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
}
