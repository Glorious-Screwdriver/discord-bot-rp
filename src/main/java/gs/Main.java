package gs;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String token = "ODM0Nzk0OTEwMzA5NDE3MDAw.YIGFWA.MtuH8-N9XmUIfEQP9FKIzuxi5nc";
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join();

        api.getServers().forEach(server -> {
            List<ServerTextChannel> channels = server.getTextChannelsByNameIgnoreCase("info");
            if (channels.size() == 1) {
                channels.get(0).addMessageCreateListener(new MainChatListener(api));
            } else {
                throw new IllegalStateException("Wrong quantity of channels named \"info\"");
            }
        });

        System.out.println("Bot started!");
    }

}
