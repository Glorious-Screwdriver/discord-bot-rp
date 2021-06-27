package gs.util.states;

import gs.DataBase;
import gs.service.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

public interface State {
    /**
     * Draws environment information
     * @param channel Target channel
     * @param player Target player
     */
    void draw(TextChannel channel, Player player);

    /**
     * Processes environment commands
     * @param event Message event
     * @param player Target Player
     * @param db DataBase object
     * @return "true" if message contained environment command, "false" if not
     */
    boolean readCommand(MessageCreateEvent event, Player player, DataBase db);
}
