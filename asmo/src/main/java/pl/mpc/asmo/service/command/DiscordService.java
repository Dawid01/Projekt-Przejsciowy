package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class DiscordService {

    private final JDA jda;

    @Value("${DISCORD_GUILD_ID:}")
    private String defaultGuildId;

    public DiscordService(@Lazy JDA jda) {
        this.jda = jda;
    }

    private String resolveGuildId(String guildId) {
        if ((guildId == null || guildId.isEmpty()) && defaultGuildId != null && !defaultGuildId.isEmpty()) {
            return defaultGuildId;
        }
        return guildId;
    }
    @RequiresBotAccess
    public String getServerInfo(String guildId) {
        String resolvedId = resolveGuildId(guildId);

        if (resolvedId == null || resolvedId.isEmpty()) {
            throw new IllegalArgumentException("Discord server ID cannot be null (and no default provided)");
        }

        Guild guild = jda.getGuildById(resolvedId);
        if (guild == null) {
            throw new IllegalArgumentException("Discord server not found by guildId: " + resolvedId);
        }

        String serverName = guild.getName();
        String serverId = guild.getId();
        Member owner = guild.retrieveOwner().complete();

        int totalMembers = guild.getMemberCount();
        int textChannelCount = guild.getTextChannels().size();
        int voiceChannelCount = guild.getVoiceChannels().size();
        int categoryCount = guild.getCategories().size();
        String creationDate = guild.getTimeCreated().toLocalDate().toString();

        int boostCount = guild.getBoostCount();
        String boostTier = guild.getBoostTier().toString();

        return String.format("""
                Server Name: %s
                Server ID: %s
                Owner: %s
                Created On: %s
                Members: %d
                Channels:
                 - Text: %d
                 - Voice: %d
                 - Categories: %d
                Boosts:
                 - Count: %d
                 - Tier: %s
                """,
                serverName, serverId, owner.getUser().getName(), creationDate,
                totalMembers, textChannelCount, voiceChannelCount, categoryCount,
                boostCount, boostTier);
    }
}