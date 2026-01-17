package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookService {

    private final JDA jda;

    public WebhookService(@Lazy JDA jda) {
        this.jda = jda;
    }
    @RequiresBotAccess
    public String createWebhook(String channelId, String name) {
        if (channelId == null || channelId.isEmpty()) throw new IllegalArgumentException("channelId cannot be null");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("webhook name cannot be null");

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found by channelId: " + channelId);
        }

        Webhook webhook = channel.createWebhook(name).complete();
        return "Created webhook '" + name + "'. URL: " + webhook.getUrl();
    }
    @RequiresBotAccess
    public String deleteWebhook(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) throw new IllegalArgumentException("webhookId cannot be null");

        try {
            Webhook webhook = jda.retrieveWebhookById(webhookId).complete();
            String name = webhook.getName();
            webhook.delete().queue();
            return "Deleted webhook: " + name;
        } catch (Exception e) {
            throw new IllegalArgumentException("Webhook not found or could not be deleted. ID: " + webhookId);
        }
    }

    @RequiresBotAccess
    public String listWebhooks(String channelId) {
        if (channelId == null || channelId.isEmpty()) throw new IllegalArgumentException("channelId cannot be null");

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found by channelId");
        }

        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        if (webhooks.isEmpty()) {
            return "No webhooks found in channel " + channel.getName();
        }

        List<String> formattedWebhooks = formatWebhooks(webhooks);
        return "**Found " + formattedWebhooks.size() + " webhooks:** \n" + String.join("\n", formattedWebhooks);
    }

    @RequiresBotAccess
    public String sendWebhookMessage(String webhookId, String message) {
        if (webhookId == null || webhookId.isEmpty()) throw new IllegalArgumentException("webhookId cannot be null");
        if (message == null || message.isEmpty()) throw new IllegalArgumentException("message content cannot be null");

        try {
            Webhook webhook = jda.retrieveWebhookById(webhookId).complete();
            webhook.sendMessage(message).queue();
            return "Message sent to webhook '" + webhook.getName() + "' successfully.";
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to send message. Webhook not found or invalid ID: " + webhookId);
        }
    }

    private List<String> formatWebhooks(List<Webhook> webhooks) {
        return webhooks.stream()
                .map(w -> String.format("- **%s** (ID: %s) \n  URL: `%s`", w.getName(), w.getId(), w.getUrl()))
                .toList();
    }
}