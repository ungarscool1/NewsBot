package wtf.ungarscool1.newsbot.site;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import wtf.ungarscool1.newsbot.Main;
import wtf.ungarscool1.newsbot.entity.Feed;
import wtf.ungarscool1.newsbot.entity.News;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

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
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                    String url = element.getElementsByTagName("link").item(0).getTextContent();
                    String image = element.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent();
                    String guid = element.getElementsByTagName("guid").item(0).getTextContent();
                    News news = new News(title, description, image, this.feed.icon, this.feed.name, this.feed.channel, url, guid);
                    Main.news.add(news);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
