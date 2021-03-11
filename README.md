# NewsBot

## Setup

To setup you bot you will need:
- a Discord Bot token
- have a server to host the bot
- have some rss feed
- have a discord server where you're admin

### Configure the bot

To configure the bot, you will need to create a ``config.json`` file.

Inside, you write:
```json
{
    "bot_token": "<your-bot-token>",
    "feeds": [
        {
            "name": "CNN",
            "icon": "https://pbs.twimg.com/profile_images/1278259160644227073/MfCyF7CG_400x400.jpg",
            "rss": "http://rss.cnn.com/rss/edition.rss",
            "format": "feedburner",
            "channel": "<your-discord-channel-id>"
        }
    ]
}
```

With this configuration, you have by default CNN edition feed. You can also add or remove feeds.

## Adding feed

To add feed on your bot, you need to append ``feeds`` field in ``config.json``.

The following format will need to be respected !
```json
{
    "name": "<your-feed-name>",
    "icon": "<your-feed-icon>",
    "rss": "<your-rss-file>",
    "format": "<rss-format>",
    "channel": "<your-discord-channel-id>"
}
```

The ``format`` is realy important, it's the way to know how to parse rss file.

There are two format supported to covert the most of rss file.

- ``feedburner``
- ``harvard-tech-rss``

For exemple, CNN use ``feedburner`` format and in France France-info use ``harvard-tech-rss``.