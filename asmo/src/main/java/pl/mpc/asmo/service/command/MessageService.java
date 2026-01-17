package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final JDA jda;

    public MessageService(@Lazy JDA jda) {
        this.jda = jda;
    }

    private TextChannel getChannel(String channelId) {
        if (channelId == null || channelId.isEmpty()) {
            throw new IllegalArgumentException("channelId cannot be null");
        }
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found by channelId: " + channelId);
        }
        return channel;
    }
    @RequiresBotAccess
    public String sendMessage(String channelId, String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message content cannot be null");
        }
        TextChannel channel = getChannel(channelId);
        Message sentMessage = channel.sendMessage(message).complete();
        return "Message sent successfully. Link: " + sentMessage.getJumpUrl();
    }
    @RequiresBotAccess
    public String editMessage(String channelId, String messageId, String newMessage) {
        if (messageId == null || messageId.isEmpty()) throw new IllegalArgumentException("messageId cannot be null");
        if (newMessage == null || newMessage.isEmpty()) throw new IllegalArgumentException("newMessage cannot be null");

        TextChannel channel = getChannel(channelId);
        Message message = channel.retrieveMessageById(messageId).complete();
        if (message == null) {
            throw new IllegalArgumentException("Message not found by messageId");
        }
        Message editedMessage = message.editMessage(newMessage).complete();
        return "Message edited successfully. Link: " + editedMessage.getJumpUrl();
    }
    @RequiresBotAccess
    public String deleteMessage(String channelId, String messageId) {
        if (messageId == null || messageId.isEmpty()) throw new IllegalArgumentException("messageId cannot be null");

        TextChannel channel = getChannel(channelId);
        Message message = channel.retrieveMessageById(messageId).complete();
        if (message == null) {
            throw new IllegalArgumentException("Message not found by messageId");
        }
        message.delete().queue();
        return "Message deleted successfully";
    }
    @RequiresBotAccess
    public String readMessages(String channelId, Integer count) {
        int limit = (count == null || count <= 0) ? 100 : count;
        TextChannel channel = getChannel(channelId);

        List<Message> messages = channel.getHistory().retrievePast(limit).complete();
        List<String> formattedMessages = formatMessages(messages);

        return "**Retrieved " + messages.size() + " messages:** \n" + String.join("\n", formattedMessages);
    }
    @RequiresBotAccess
    public String addReaction(String channelId, String messageId, String emoji) {
        return manageReaction(channelId, messageId, emoji, true);
    }
    @RequiresBotAccess
    public String removeReaction(String channelId, String messageId, String emoji) {
        return manageReaction(channelId, messageId, emoji, false);
    }
    private String manageReaction(String channelId, String messageId, String emojiStr, boolean add) {
        if (messageId == null || messageId.isEmpty()) throw new IllegalArgumentException("messageId cannot be null");
        if (emojiStr == null || emojiStr.isEmpty()) throw new IllegalArgumentException("emoji cannot be null");

        TextChannel channel = getChannel(channelId);
        Message message = channel.retrieveMessageById(messageId).complete();
        if (message == null) throw new IllegalArgumentException("Message not found");

        Emoji emoji = Emoji.fromUnicode(emojiStr);

        if (add) {
            message.addReaction(emoji).queue();
            return "Added reaction " + emojiStr + " successfully.";
        } else {
            message.removeReaction(emoji).queue();
            return "Removed reaction " + emojiStr + " successfully.";
        }
    }

    private List<String> formatMessages(List<Message> messages) {
        return messages.stream()
                .map(m -> {
                    String author = m.getAuthor().getName();
                    String time = m.getTimeCreated().toLocalTime().toString();
                    String content = m.getContentDisplay();
                    if (content.length() > 200) content = content.substring(0, 197) + "...";
                    return String.format("- [%s] %s: %s (ID: %s)", time, author, content, m.getId());
                }).toList();
    }
}