package com.github.ungarscool1.newsbot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.UserStatus;

public class Main {

	static String rss = "https://www.francetvinfo.fr/titres.rss";
	static int sendedMessage = 0;
	static List<EmbedBuilder> NEWS = new ArrayList<>();


	public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken("YOUR_TOKEN").login().join();
        api.updateActivity(ActivityType.STREAMING, "News from FranceInfo");
        System.out.println("Récupération des informations via FranceInfo");

        
        api.addMessageCreateListener(event -> {
        	if (event.getMessageAuthor().asUser().get().isYourself()) {
        		sendedMessage++;
        		if (sendedMessage == NEWS.size()) {
					api.updateStatus(UserStatus.OFFLINE);
					api.disconnect();
					System.exit(0);
				}
        	}
        });
        
        api.getServers().forEach(server -> {
        	server.getChannels().forEach(channel -> {
        		if (channel.getName().equals("news")) {
        			if (channel.asTextChannel().isPresent()) {
						try {
							List<EmbedBuilder> news = readRss(rss);
							NEWS = news;
							if (news.size() == 0) {
								api.updateStatus(UserStatus.OFFLINE);
								api.disconnect();
								System.exit(0);
							}
							for (EmbedBuilder info : news) {
								channel.asTextChannel().get().sendMessage(info);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
				}
        	});
        });
        
    }

	private static List<EmbedBuilder> readRss(String rss2) throws Exception {
		URL rssUrl = new URL(rss2);
		BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
		String origin = "";
		String line;
		boolean inItem = false;
		List<EmbedBuilder> news = new ArrayList<>();
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			if (line.contains("<item>")) {
				inItem = true;
			} else if (line.contains("</item>") && inItem) {
				news.add(createEmbededNews(origin));
				origin = "";
				inItem = false;
			}
			
			if (inItem) {
				if (line.contains("<title>")) {
					String temp = line.substring((line.indexOf("<title>") + 7), line.indexOf("</title>"));
					origin = "title=" + temp;
				} else
				if (line.contains("<description>")) {
					String temp = line.substring((line.indexOf("<description>") + 21));
					if (temp.contains("</description>")) {
						temp = temp.substring(0, temp.indexOf("]]></description>"));
						System.out.println("Il y a la fin de description");
					} else if (temp.contains("]]>")) {
						temp = temp.substring(0, temp.indexOf("]]>"));
						System.out.println("Il y a la fin de CDATA");
					}
					temp = temp.replaceAll("&nbsp;", " ");
					origin += "&description=" + temp;
				} else
				if (line.contains("<link>")) {
					String temp = line.substring((line.indexOf("<link>") + 6), line.indexOf("</link>"));
					if (alreadySent(temp)) {
						origin = "";
						System.out.println("L'article " + temp + " a deja été envoyé");
						inItem = false;
					} else {
						writeSentNews(temp);
						origin += "&link=" + temp;
					}
				} else
				if (line.contains("<enclosure")) {
					String temp = line.substring((line.indexOf("url=\"") + 5), line.indexOf("\"/>"));
					origin += "&img=" + temp;
				}
			}
			
			
		}
		
		in.close();
		return news;
	}
	
	public static EmbedBuilder createEmbededNews(String line) {
		String title = line.substring((line.indexOf("title=") + 6), line.indexOf("&description="));
		String description = line.substring((line.indexOf("description=") + 13), line.indexOf("&link="));
		String link = line.substring((line.indexOf("link=") + 5), line.indexOf("&img="));
		String imageUrl = line.substring((line.indexOf("img=") + 4));
		return new EmbedBuilder().setTitle(title).setDescription(description).setUrl(link).setImage(imageUrl).setColor(Color.green).setFooter("France info", "https://pbs.twimg.com/profile_images/1019886363515211776/D2TBSqHw_400x400.jpg");
	}
	
	public static boolean alreadySent(String guid) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(new File("sended.list")));
		String line = "";
		while ((line = in.readLine()) != null) {
			if (line.equals(guid)) return true;
		}
		return false;
	}
	
	public static void writeSentNews(String guid) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("sended.list"), true));
		writer.write(guid);
		writer.newLine();
		writer.close();
	}
	
}
