package wtf.ungarscool1.newsbot.entity;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class News {
    private String title;
    private String description;
    private String image;
    private String footer_image;
    private String footer_text;
    private String channel;
    private String url;
    private Color color;

    public News(String title, String description, String image, String footer_image, String footer_text, String channel, String url) {
        this.title = title.replaceAll("&nbsp;", "");
        this.description = (description.length() < 500) ? description.replaceAll("&nbsp;", "") : description.substring(0, 500).replaceAll("&nbsp;", "");
        this.image = image;
        this.footer_image = footer_image;
        this.footer_text = footer_text;
        this.channel = channel;
        this.url = url;
        this.color = title.contains("DIRECT") ? Color.RED : Color.GREEN;
        if (title.contains("DIRECT")) {
            this.title = title.replaceAll("DIRECT.", "\uD83D\uDD34");
        } else if (title.contains("VIDEO")) {
            this.title = title.replaceAll("VIDEO.", "\uD83C\uDF9E");
        }
        cleanStrings();
    }

    public EmbedBuilder generateEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(this.title);
        embed.setDescription(this.description);
        embed.setImage(this.image);
        embed.setColor(this.color);
        embed.setFooter(this.footer_text, this.footer_image);
        embed.setUrl(this.url);
        return embed;
    }

    private void cleanStrings() {
        Pattern p = Pattern.compile("(&lt;.+&gt;)|(<.+>)");
        Matcher title = p.matcher(this.title);
        Matcher description = p.matcher(this.description);
        if (title.find())
            this.title = title.replaceAll("");
        if (description.find())
            this.description = description.replaceAll("");
    }

    public String getChannel() {
        return channel;
    }
}
