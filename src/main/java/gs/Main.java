package gs;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Player> activePlayers = new ArrayList<>();

        String token = "ODM0Nzk0OTEwMzA5NDE3MDAw.YIGFWA.MtuH8-N9XmUIfEQP9FKIzuxi5nc";
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join();

        api.addMessageCreateListener(new MainChatListener(activePlayers));

        System.out.println("Bot started!");
    }

}
