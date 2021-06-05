package gs;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MainChatListener implements MessageCreateListener {
//    Path helpFilePath = Paths.get("src/main/java/gs/materials/MCL-help.txt");
    List<Player> activePlayers;

    public MainChatListener(List<Player> activePlayers) {
        this.activePlayers = activePlayers;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (msg.equalsIgnoreCase("!help")) {
            new MessageBuilder()
                    .append("Help incoming!\n")
                    .append("Type in !console to start playing.")
                    .send(event.getChannel());

        } else if (msg.equalsIgnoreCase("!console")) {
            Optional<Server> server = event.getServer();
            if (!server.isPresent()) throw new RuntimeException("Server is not present");

            MessageAuthor author = event.getMessageAuthor();

            Optional<String> discriminator = author.getDiscriminator();
            if (!discriminator.isPresent()) throw new RuntimeException("Discriminator is not present");

            ServerTextChannel createdChannel = new ServerTextChannelBuilder(server.get())
                    .setName("Console-" + discriminator.get())
                    .create()
                    .join();

            Player player = new Player(
                    author.getId(),
                    author.getDisplayName(),
                    new Console(createdChannel)
            );

            activePlayers.add(player);
            System.out.println(activePlayers.toString() + " in activePlayers now.");
            createdChannel.addMessageCreateListener(new ConsoleListener(activePlayers, player));
            player.console.sendMessage("> Console opened, sir.");
        }
    }
}
