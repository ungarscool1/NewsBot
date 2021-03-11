package wtf.ungarscool1.newsbot.site;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import wtf.ungarscool1.newsbot.Main;
import wtf.ungarscool1.newsbot.entity.Feed;
import wtf.ungarscool1.newsbot.entity.News;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.net.URL;

import static wtf.ungarscool1.newsbot.Main.alreadySent;
import static wtf.ungarscool1.newsbot.Main.writeSentNews;

public class HarvardTechRss {
    private Feed feed;

    public HarvardTechRss(Feed feed) {
        this.feed = feed;
    }

    public void parseToEmbed() {
        try {
            URL rssUrl = new URL(this.feed.rss);
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
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                    String url = element.getElementsByTagName("link").item(0).getTextContent();
                    String image = element.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent();
                    News news = new News(title, description, image, this.feed.icon, this.feed.name, this.feed.channel, url);
                    if (!alreadySent(element.getElementsByTagName("guid").item(0).getTextContent())) {
                        writeSentNews(element.getElementsByTagName("guid").item(0).getTextContent());
                        Main.news.add(news);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
