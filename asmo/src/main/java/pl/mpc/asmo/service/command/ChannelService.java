package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final JDA jda;

    @Value("${DISCORD_GUILD_ID:}")
    private String defaultGuildId;

    public ChannelService(@Lazy JDA jda) {
        this.jda = jda;
    }

    private Guild getGuild(String guildId) {
        if ((guildId == null || guildId.isEmpty()) && defaultGuildId != null && !defaultGuildId.isEmpty()) {
            guildId = defaultGuildId;
        }
        if (guildId == null || guildId.isEmpty()) {
            throw new IllegalArgumentException("guildId cannot be null (and no default provided)");
        }
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new IllegalArgumentException("Discord server not found by guildId: " + guildId);
        }
        return guild;
    }

    private Category findCategoryByName(Guild guild, String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) return null;
        List<Category> categories = guild.getCategoriesByName(categoryName, true);
        return categories.isEmpty() ? null : categories.get(0);
    }


    @RequiresBotAccess
    public String deleteChannel(String guildId, String channelId) {
        if (channelId == null || channelId.isEmpty()) {
            throw new IllegalArgumentException("channelId cannot be null");
        }
        Guild guild = getGuild(guildId);
        GuildChannel channel = guild.getGuildChannelById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("Channel not found by channelId: " + channelId);
        }

        String name = channel.getName();
        String type = channel.getType().name();
        channel.delete().queue();

        return "Deleted " + type + " channel: " + name;
    }

    @RequiresBotAccess
    public String renameChannel(String guildId, String channelId, String newName) {
        Guild guild = getGuild(guildId);
        GuildChannel channel = guild.getGuildChannelById(channelId);
        if (channel == null) {
            throw new RuntimeException("Channel not found with ID: " + channelId);
        }

        channel.getManager().setName(newName).queue();

        return "Channel name changed to: " + newName;
    }

    @RequiresBotAccess
    public String createTextChannel(String guildId, String name, String categoryName) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        Guild guild = getGuild(guildId);

        Category category = findCategoryByName(guild, categoryName);

        TextChannel textChannel;
        if (category != null) {
            textChannel = category.createTextChannel(name).complete();
            return "Created text channel: '" + textChannel.getName() + "' inside category: '" + category.getName() + "'";
        } else if (categoryName != null && !categoryName.isEmpty()) {
            return "Error: Category '" + categoryName + "' not found. Please create it first.";
        } else {
            textChannel = guild.createTextChannel(name).complete();
            return "Created text channel: '" + textChannel.getName() + "' (No Category)";
        }
    }

    @RequiresBotAccess
    public String createVoiceChannel(String guildId, String name, String categoryName) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        Guild guild = getGuild(guildId);

        Category category = findCategoryByName(guild, categoryName);

        VoiceChannel voiceChannel;
        if (category != null) {
            voiceChannel = category.createVoiceChannel(name).complete();
            return "Created voice channel: '" + voiceChannel.getName() + "' inside category: '" + category.getName() + "'";
        } else if (categoryName != null && !categoryName.isEmpty()) {
            return "Error: Category '" + categoryName + "' not found. Please create it first.";
        } else {
            voiceChannel = guild.createVoiceChannel(name).complete();
            return "Created voice channel: '" + voiceChannel.getName() + "' (No Category)";
        }
    }

    @RequiresBotAccess
    public String moveTextChannelToCategory(String guildId, String channelName, String categoryName) {
        if (channelName == null || channelName.isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be empty");
        }
        if (categoryName == null || categoryName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        Guild guild = getGuild(guildId);

        Category targetCategory = findCategoryByName(guild, categoryName);
        if (targetCategory == null) {
            return "Error: Category '" + categoryName + "' not found.";
        }

        List<ICategorizableChannel> channels = guild.getChannels().stream()
                .filter(c -> c instanceof ICategorizableChannel)
                .map(c -> (ICategorizableChannel) c)
                .filter(c -> c.getName().equalsIgnoreCase(channelName))
                .toList();

        if (channels.isEmpty()) {
            return "Error: Channel '" + channelName + "' not found or cannot be moved.";
        }

        ICategorizableChannel targetChannel = channels.get(0);

        try {
            targetChannel.getManager()
                    .setParent(targetCategory)
                    .complete();

            return "Successfully moved channel '" + channelName + "' to category '" + categoryName + "'.";

        } catch (Exception e) {
            return "Failed to move channel. Error: " + e.getMessage();
        }
    }

    @RequiresBotAccess
    public String findChannel(String guildId, String channelName) {
        if (channelName == null || channelName.isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be empty");
        }

        Guild guild = getGuild(guildId);

        List<GuildChannel> channels = guild.getChannels().stream()
                .filter(c -> c.getName().equalsIgnoreCase(channelName))
                .toList();

        if (channels.isEmpty()) {
            return "Channel not found: " + channelName;
        }

        return channels.stream()
                .map(GuildChannel::getId)
                .collect(Collectors.joining(", "));
    }

    @RequiresBotAccess
    public String listChannels(String guildId) {
        Guild guild = getGuild(guildId);
        List<GuildChannel> channels = guild.getChannels();

        if (channels.isEmpty()) {
            return "No channels found on this server.";
        }

        return "Server Channels (" + channels.size() + "):\n" +
                channels.stream()
                        .map(c -> String.format("- [%s] %s (ID: %s)", c.getType().name(), c.getName(), c.getId()))
                        .collect(Collectors.joining("\n"));
    }
}