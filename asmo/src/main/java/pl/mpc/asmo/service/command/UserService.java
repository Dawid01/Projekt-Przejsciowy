package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final JDA jda;

    @Value("${DISCORD_GUILD_ID:}")
    private String defaultGuildId;

    public UserService(@Lazy JDA jda) {
        this.jda = jda;
    }

    private String resolveGuildId(String guildId) {
        if ((guildId == null || guildId.isEmpty()) && defaultGuildId != null && !defaultGuildId.isEmpty()) {
            return defaultGuildId;
        }
        return guildId;
    }

    private User retrieveUser(String userId) {
        try {
            return jda.retrieveUserById(userId).complete();
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found by userId: " + userId);
        }
    }
    @RequiresBotAccess
    public String getUserIdByName(String username, String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new IllegalArgumentException("Guild not found: " + guildId);
        }

        try {
            var members = guild.retrieveMembersByPrefix(username, 1).get();

            if (members.isEmpty()) {
                throw new IllegalArgumentException("Nie znaleziono użytkownika: " + username);
            }

            return members.get(0).getId();

        } catch (Exception e) {
            throw new RuntimeException("Błąd szukania użytkownika: " + e.getMessage());
        }
    }
    @RequiresBotAccess
    public String sendPrivateMessage(String userId, String message) {
        if (message == null || message.isEmpty()) throw new IllegalArgumentException("message cannot be null");

        User user = retrieveUser(userId);
        Message sentMessage = user.openPrivateChannel().complete().sendMessage(message).complete();
        return "DM sent successfully. Link: " + sentMessage.getJumpUrl();
    }
    @RequiresBotAccess
    public String editPrivateMessage(String userId, String messageId, String newMessage) {
        if (newMessage == null || newMessage.isEmpty()) throw new IllegalArgumentException("newMessage cannot be null");

        User user = retrieveUser(userId);
        Message message = user.openPrivateChannel().complete().retrieveMessageById(messageId).complete();

        if (message == null) throw new IllegalArgumentException("Message not found");

        Message editedMessage = message.editMessage(newMessage).complete();
        return "DM edited successfully. Link: " + editedMessage.getJumpUrl();
    }
    @RequiresBotAccess
    public String deletePrivateMessage(String userId, String messageId) {
        User user = retrieveUser(userId);
        Message message = user.openPrivateChannel().complete().retrieveMessageById(messageId).complete();

        if (message == null) throw new IllegalArgumentException("Message not found");

        message.delete().queue();
        return "DM deleted successfully";
    }
    @RequiresBotAccess
    public String readPrivateMessages(String userId, Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count; // Domyślnie 10, bezpieczniej
        User user = retrieveUser(userId);

        List<Message> messages = user.openPrivateChannel().complete().getHistory().retrievePast(limit).complete();
        List<String> formattedMessages = formatMessages(messages);

        return "**Last " + messages.size() + " DMs with " + user.getName() + ":** \n" + String.join("\n", formattedMessages);
    }

    private List<String> formatMessages(List<Message> messages) {
        return messages.stream()
                .map(m -> {
                    String author = m.getAuthor().getName();
                    String time = m.getTimeCreated().toLocalTime().toString();
                    String content = m.getContentDisplay();
                    if (content.length() > 100) content = content.substring(0, 97) + "...";
                    return String.format("- [%s] %s: %s (ID: %s)", time, author, content, m.getId());
                }).toList();
    }
    @RequiresBotAccess
    public String kickUser(String guildId, String userId, String reason) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            return "Error: Guild not found.";
        }

        try {
            UserSnowflake user = UserSnowflake.fromId(userId);

            guild.kick(user)
                    .reason(reason != null ? reason : "No reason provided")
                    .complete();

            return "Successfully kicked user (ID: " + userId + "). Reason: " + reason;

        } catch (HierarchyException e) {
            return "Error: Cannot kick this user. They likely have a higher or equal role to the bot.";
        } catch (InsufficientPermissionException e) {
            return "Error: Bot does not have 'Kick Members' permission.";
        } catch (Exception e) {
            return "Failed to kick user. Error: " + e.getMessage();
        }
    }

    @RequiresBotAccess
    public String banUser(String guildId, String userId, String reason) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            return "Error: Guild not found.";
        }

        try {
            UserSnowflake user = UserSnowflake.fromId(userId);

            guild.ban(user, 0, TimeUnit.SECONDS)
                    .reason(reason != null ? reason : "No reason provided")
                    .complete();

            return "Successfully banned user (ID: " + userId + "). Reason: " + reason;

        } catch (HierarchyException e) {
            return "Error: Cannot ban this user. They likely have a higher or equal role to the bot.";
        } catch (InsufficientPermissionException e) {
            return "Error: Bot does not have 'Ban Members' permission.";
        } catch (Exception e) {
            return "Failed to ban user. Error: " + e.getMessage();
        }
    }
    @RequiresBotAccess
    public String timeoutUser(String guildId, String userId, long durationInMinutes, String reason) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            return "Error: Guild not found.";
        }

        if (durationInMinutes > 40320) {
            return "Error: Max timeout duration is 28 days.";
        }
        if (durationInMinutes <= 0) {
            return "Error: Duration must be positive. To unmute, use a specialized unmute command or set a very short duration.";
        }

        try {

            Member member = guild.retrieveMemberById(userId).complete();

            member.timeoutFor(durationInMinutes, TimeUnit.MINUTES)
                    .reason(reason != null ? reason : "No reason provided")
                    .complete();

            return "Successfully muted (timed out) user " + member.getUser().getName() +
                    " for " + durationInMinutes + " minutes. Reason: " + reason;

        } catch (HierarchyException e) {
            return "Error: Cannot mute this user. They likely have a higher or equal role to the bot.";
        } catch (InsufficientPermissionException e) {
            return "Error: Bot does not have 'Moderate Members' permission.";
        } catch (IllegalArgumentException e) {
            return "Error: Invalid argument provided (e.g., duration too long).";
        } catch (Exception e) {
            return "Failed to mute user. Error: " + e.getMessage();
        }
    }
    @RequiresBotAccess
    public String removeTimeout(String guildId, String userId, String reason) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return "Error: Guild not found.";

        try {
            Member member = guild.retrieveMemberById(userId).complete();

            member.removeTimeout()
                    .reason(reason)
                    .complete();

            return "Successfully removed timeout (unmuted) for user " + member.getUser().getName();

        } catch (Exception e) {
            return "Failed to unmute user. Error: " + e.getMessage();
        }
    }
}