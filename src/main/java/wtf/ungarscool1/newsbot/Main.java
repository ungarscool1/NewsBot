package wtf.ungarscool1.newsbot;

import com.google.gson.Gson;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.UserStatus;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import wtf.ungarscool1.newsbot.entity.Feed;
import wtf.ungarscool1.newsbot.entity.News;
import wtf.ungarscool1.newsbot.site.Feedburner;
import wtf.ungarscool1.newsbot.site.HarvardTechRss;

public class Main {

    public static int toSend = 0;
    public static int sendedMessage = 0;
    public static DiscordApi api = null;
    public static List<News> news = new ArrayList<>();
    public static Configuration config;

    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(new File("/home/pi/newsbot/config.json")), Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        api = new DiscordApiBuilder().setToken(config.bot_token).login().join();
//        api.updateStatus(UserStatus.INVISIBLE);
        api.addMessageCreateListener(event -> {
            if (event.getMessageAuthor().asUser().get().isYourself()) {
                sendedMessage++;
                if (sendedMessage == news.size()) {
                    api.disconnect();
                    System.exit(0);
                }
            }
        });

        for (int i = 0; i < config.feeds.length; i++) {
            Feed current = config.feeds[i];
            System.out.println("Parsing de " + current.name + " @ " + current.rss);
            if (current.format.equalsIgnoreCase("feedburner")) {
                Feedburner feedburner = new Feedburner(current);
                feedburner.parseToEmbed();
            } else {
                HarvardTechRss harvardTechRss = new HarvardTechRss(current);
                harvardTechRss.parseToEmbed();
            }
        }
        sendMessage();

        if (news.size() == 0) {
            api.disconnect();
            System.exit(0);
        }
    }

    public static void sendMessage() {
        news.forEach(news1 -> {
            api.getChannelById(news1.getChannel()).flatMap(Channel::asServerTextChannel).ifPresent(serverTextChannel -> serverTextChannel.sendMessage(news1.generateEmbed()));
        });
    }

    public static boolean alreadySent(String guid)  {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("/home/pi/newsbot/sended.list")));
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.equals(guid)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void writeSentNews(String guid) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/home/pi/newsbot/sended.list"), true));
            writer.write(guid);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
