package com.github.ungarscool1.newsbot;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

import org.javacord.api.entity.activity.ActivityType;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class Main {

    public static int toSend = 0;
    public static int sendedMessage = 0;
    public static DiscordApi api = null;

    public static void main(String[] args) {
        api = new DiscordApiBuilder().setToken("YOUR_TOKEN").login().join();
        api.updateActivity(ActivityType.STREAMING, "News from FranceInfo");
        api.addMessageCreateListener(event -> {
            if (event.getMessageAuthor().asUser().get().isYourself()) {
                sendedMessage++;
                if (sendedMessage == 1) {
                    api.disconnect();
                    System.exit(0);
                }
            }
        });
        try {
            URL rssUrl = new URL("https://www.francetvinfo.fr/titres.rss");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(rssUrl.openStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    EmbedBuilder embed = new EmbedBuilder();
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    if (title.contains("DIRECT")) {
                        title = title.replaceAll("DIRECT.", "\uD83D\uDD34");
                    } else if (title.contains("VIDEO")) {
                        title = title.replaceAll("VIDEO.", "\uD83C\uDF9E");
                    }
                    embed.setTitle(title);
                    embed.setDescription(element.getElementsByTagName("description").item(0).getTextContent().replaceAll("&nbsp;", " "));
                    if (element.getElementsByTagName("title").item(0).getTextContent().contains("DIRECT"))
                        embed.setColor(Color.RED);
                    else
                        embed.setColor(Color.GREEN);
                    embed.setUrl(element.getElementsByTagName("link").item(0).getTextContent());
                    embed.setImage(element.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent());
                    embed.setFooter("France info", "https://pbs.twimg.com/profile_images/1019886363515211776/D2TBSqHw_400x400.jpg");
                    if (!alreadySent(element.getElementsByTagName("guid").item(0).getTextContent())) {
                        toSend++;
                        writeSentNews(element.getElementsByTagName("guid").item(0).getTextContent());
                        api.getServers().forEach(server -> {
                            server.getChannelById(YOUR_CHANNEL_FOR_NEWS).get().asServerTextChannel().get().sendMessage(embed);
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (toSend == 0) {
            api.disconnect();
            System.exit(0);
        }
    }

    public static boolean alreadySent(String guid)  {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("./sended.list")));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./sended.list"), true));
            writer.write(guid);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
