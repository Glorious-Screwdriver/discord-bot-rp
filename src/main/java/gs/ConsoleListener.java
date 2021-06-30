package gs;

import gs.service.Player;
import gs.util.*;
import gs.service.ui.*;
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
    UI ui;

    public ConsoleListener(
            Player player, TextChannel channel,
            DataBase db, List<Player> online, List<Player> offline
    ) {
        this.player = player;
        this.channel = channel;
        this.db = db;
        this.online = online;
        this.offline = offline;

        this.ui = new HomeUI();
    }

    private void changeUI(UI UI) {
        this.ui = UI;
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

        boolean processed = ui.readCommand(event, player, db);

        if (!processed) {
            switch (msg) {
                case "home":
                    changeUI(new HomeUI());
                    break;
                case "case":
                    changeUI(new CaseUI());
                    break;
                case "inventory":
                    changeUI(new InventoryUI());
                    break;
                case "shop":
                    changeUI(new ShopUI());
                    break;
                case "farm":
                    changeUI(new FarmUI());
                    break;
                case "achievements":
                    changeUI(new AchievementsUI());
                    break;

                case "help":
                    Helper.sendHomeHelp(channel);
                    return;
                default:
                    channel.sendMessage("Undefined command. For more information type in \"help\".");
                    return;
            }
            ui.draw(channel, player);
        }
    }

    public void drawCurrentUI() {
        ui.draw(channel, player);
    }
}
