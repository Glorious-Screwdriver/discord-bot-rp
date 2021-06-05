package gs;

import gs.util.ConsoleState;
import org.javacord.api.entity.channel.TextChannel;

public class Console {
    ConsoleState consoleState;
    TextChannel channel;
    UI ui;

    public Console(TextChannel channel) {
        this.channel = channel;
        consoleState = ConsoleState.HOME;
    }

    public void sendMessage(String msg) {
        channel.sendMessage(msg);
    }

    public TextChannel getChannel() {
        return channel;
    }
}
