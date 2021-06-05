package gs;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.List;

public class ConsoleListener implements MessageCreateListener {
    List<Player> activePlayers;
    Player player;
    TextChannel channel;

    public ConsoleListener(List<Player> activePlayers, Player player) {
        this.activePlayers = activePlayers;
        this.player = player;
        this.channel = player.console.getChannel();
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String msg = event.getMessageContent();

        if (msg.equalsIgnoreCase("quit")) {
            channel.sendMessage("> Closing console...");
            channel.asServerChannel().get().delete("Close console command");
            activePlayers.remove(player);
        }
    }
}
