package gs;

import gs.service.Player;
import gs.util.*;
import gs.service.states.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.List;

public class ConsoleListener implements MessageCreateListener {
    Player player;
    TextChannel channel;
    DataBase db;
    List<Player> online;
    List<Player> offline;
    State state;

    public ConsoleListener(
            Player player, TextChannel channel,
            DataBase db, List<Player> online, List<Player> offline
    ) {
        this.player = player;
        this.channel = channel;
        this.db = db;
        this.online = online;
        this.offline = offline;
        this.state = new HomeState();
    }

    private void changeState(State state) {
        this.state = state;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isYourself()) return;

        String msg = event.getMessageContent().toLowerCase();

        if (msg.equals("quit")) {
            channel.sendMessage("Closing console...");

            player.farm.stop();
            player.stopEnergyThread();

            channel.asServerChannel()
                    .orElseThrow(() -> new RuntimeException("Server channel is not present"))
                    .delete("Close console command");

            online.remove(player);
            if (db == null) {
                offline.add(player);
            }

            System.out.println("Player disconnected. Active players now: " + online.toString());
            return;
        }

        boolean processed = state.readCommand(event, player, db);

        if (!processed) {
            switch (msg) {
                case "home":
                    changeState(new HomeState());
                    break;
                case "case":
                    changeState(new CaseState());
                    break;
                case "inventory":
                    changeState(new InventoryState());
                    break;
                case "shop":
                    changeState(new ShopState());
                    break;
                case "farm":
                    changeState(new FarmState());
                    break;
                case "achievements":
                    changeState(new AchievementsState());
                    break;

                case "help":
                    Helper.sendHomeHelp(channel);
                    return;
                default:
                    channel.sendMessage("Undefined command. For more information type in \"help\".");
                    return;
            }
            state.draw(channel, player);
        }
    }

    public void drawCurrentState() {
        state.draw(channel, player);
    }
}
