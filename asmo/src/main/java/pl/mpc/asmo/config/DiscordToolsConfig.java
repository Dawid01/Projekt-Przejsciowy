package pl.mpc.asmo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import pl.mpc.asmo.context.BotContext;
import pl.mpc.asmo.service.command.*;

import java.util.function.Function;

@Configuration
public class DiscordToolsConfig {

    // ==================================================================================
    //                               REKORDY DANYCH (DTO)
    // ==================================================================================

    // --- KATEGORIE ---
    public record CreateCategoryRequest(String name) {
    }

    public record DeleteCategoryRequest(String categoryId) {
    }

    public record FindCategoryRequest(String name) {
    }

    public record ListCategoryChannelsRequest(String categoryId) {
    }

    // --- KANAŁY ---
    public record CreateTextChannelRequest(String name, String categoryName) {
    }

    public record CreateVoiceChannelRequest(String name, String categoryName) {
    }

    public record MoveChannelToCategoryRequest(String channelName, String categoryName) {
    }

    public record RenameChannelRequest(String channelId, String newName) {
    }

    public record DeleteChannelRequest(String channelId) {
    }

    public record FindChannelRequest(String name) {
    }

    public record ListChannelsRequest(String filter) {
    }

    // --- INFO O SERWERZE ---
    public record GetServerInfoRequest(String query) {
    }

    // --- WIADOMOŚCI (KANAŁY TEKSTOWE) ---
    public record SendMessageRequest(String channelId, String message) {
    }

    public record EditMessageRequest(String channelId, String messageId, String newMessage) {
    }

    public record DeleteMessageRequest(String channelId, String messageId) {
    }

    public record ReadMessagesRequest(String channelId, Integer count) {
    }

    public record ReactionRequest(String channelId, String messageId, String emoji) {
    }

    // --- UŻYTKOWNICY I WIADOMOŚCI PRYWATNE (DM) ---
    public record GetUserRequest(String username) {
    }

    public record SendDMRequest(String userId, String message) {
    }

    public record EditDMRequest(String userId, String messageId, String newMessage) {
    }

    public record DeleteDMRequest(String userId, String messageId) {
    }

    public record ReadDMsRequest(String userId, Integer count) {
    }

    // --- MODERACJA ---
    public record KickUserRequest(String userId, String reason) {
    }

    public record BanUserRequest(String userId, String reason) {
    }

    public record TimeoutUserRequest(String userId, Long durationMinutes, String reason) {
    }

    public record UnmuteUserRequest(String userId, String reason) {
    }

    // --- WEBHOOKI ---
    public record CreateWebhookRequest(String channelId, String name) {
    }

    public record DeleteWebhookRequest(String webhookId) {
    }

    public record ListWebhooksRequest(String channelId) {
    }

    public record SendWebhookMsgRequest(String webhookId, String message) {
    }

    // --- ACTIVITY ---
    public record StartActivityRequest(String gameName) {
    }


    // ==================================================================================
    //                               DEFINICJE NARZĘDZI (BEANY)
    // ==================================================================================

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: KATEGORIE (CategoryService)
    // ----------------------------------------------------------------------------------

    @Bean("createCategoryTool")
    @Description("Creates a new category for channels.")
    public Function<CreateCategoryRequest, String> createCategoryTool(CategoryService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.createCategory(guildId, req.name());
        };
    }

    @Bean("deleteCategoryTool")
    @Description("Deletes a category using its ID.")
    public Function<DeleteCategoryRequest, String> deleteCategoryTool(CategoryService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.deleteCategory(guildId, req.categoryId());
        };
    }

    @Bean("findCategoryTool")
    @Description("Finds a category ID by name.")
    public Function<FindCategoryRequest, String> findCategoryTool(CategoryService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.findCategory(guildId, req.name());
        };
    }

    @Bean("listChannelsInCategoryTool")
    @Description("Lists all channels inside a specific category.")
    public Function<ListCategoryChannelsRequest, String> listChannelsInCategoryTool(CategoryService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.listChannelsInCategory(guildId, req.categoryId());
        };
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: KANAŁY (ChannelService)
    // ----------------------------------------------------------------------------------

    @Bean("renameChannelTool")
    @Description("Changes the name of a specific Discord channel. Requires valid channelId and the new name.")
    public Function<RenameChannelRequest, String> renameChannelTool(ChannelService discordService) {
        return request -> {
            String guildId = BotContext.getGuildId();
            return discordService.renameChannel(guildId, request.channelId(), request.newName());
        };
    }

    @Bean("createTextChannelTool")
    @Description("Creates a new text channel. IMPORTANT: Provide 'categoryName' (String) if you want to put it in a category.")
    public Function<CreateTextChannelRequest, String> createTextChannelTool(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            // Tutaj przekazujemy nazwę kategorii, serwis musi ją sobie znaleźć
            return service.createTextChannel(guildId, req.name(), req.categoryName());
        };
    }

    @Bean("createVoiceChannelTool")
    @Description("Creates a new voice channel. IMPORTANT: Provide 'categoryName' (String) if you want to put it in a category.")
    public Function<CreateVoiceChannelRequest, String> createVoiceChannelTool(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.createVoiceChannel(guildId, req.name(), req.categoryName());
        };
    }

    @Bean("moveChannelToCategoryTool")
    @Description("Move existing channel to category by providing their NAMES.")
    public Function<MoveChannelToCategoryRequest, String> moveTextChannelToCategory(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.moveTextChannelToCategory(guildId, req.channelName(), req.categoryName());
        };
    }

    @Bean("deleteChannelTool")
    @Description("Deletes any channel (text, voice, category) using its ID.")
    public Function<DeleteChannelRequest, String> deleteChannelTool(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.deleteChannel(guildId, req.channelId());
        };
    }

    @Bean("findChannelTool")
    @Description("Finds the Discord Channel IDs based on the channel name. Returns the numeric Channel ID as a String.")
    public Function<FindChannelRequest, String> findChannelTool(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.findChannel(guildId, req.name());
        };
    }

    @Bean("listChannelsTool")
    @Description("Lists ALL channels on the server. Always set 'filter' to 'all'.")
    public Function<ListChannelsRequest, String> listChannelsTool(ChannelService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.listChannels(guildId);
        };
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: INFO O SERWERZE (DiscordService)
    // ----------------------------------------------------------------------------------

    @Bean("getServerInfoTool")
    @Description("Get detailed discord server information. Always set 'query' to 'info'.")
    public Function<GetServerInfoRequest, String> getServerInfoTool(DiscordService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.getServerInfo(guildId);
        };
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: WIADOMOŚCI (MessageService)
    // ----------------------------------------------------------------------------------

    @Bean("sendMessageTool")
    @Description("Sends a message to a specific Discord channel.")
    public Function<SendMessageRequest, String> sendMessageTool(MessageService service) {
        return req -> service.sendMessage(req.channelId(), req.message());
    }

    @Bean("editMessageTool")
    @Description("Edits an existing message.")
    public Function<EditMessageRequest, String> editMessageTool(MessageService service) {
        return req -> service.editMessage(req.channelId(), req.messageId(), req.newMessage());
    }

    @Bean("deleteMessageTool")
    @Description("Deletes a message from a channel.")
    public Function<DeleteMessageRequest, String> deleteMessageTool(MessageService service) {
        return req -> service.deleteMessage(req.channelId(), req.messageId());
    }

    @Bean("readMessagesTool")
    @Description("Reads the last N messages from a channel history.")
    public Function<ReadMessagesRequest, String> readMessagesTool(MessageService service) {
        return req -> service.readMessages(req.channelId(), req.count());
    }

    @Bean("addReactionTool")
    @Description("Adds an emoji reaction to a message.")
    public Function<ReactionRequest, String> addReactionTool(MessageService service) {
        return req -> service.addReaction(req.channelId(), req.messageId(), req.emoji());
    }

    @Bean("removeReactionTool")
    @Description("Removes an emoji reaction from a message.")
    public Function<ReactionRequest, String> removeReactionTool(MessageService service) {
        return req -> service.removeReaction(req.channelId(), req.messageId(), req.emoji());
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: UŻYTKOWNICY, DM I MODERACJA (UserService)
    // ----------------------------------------------------------------------------------

    @Bean("getUserIdByNameTool")
    @Description("Gets a Discord user's ID by their username (and optional discriminator) in a specific server.")
    public Function<GetUserRequest, String> getUserIdByNameTool(UserService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.getUserIdByName(req.username(), guildId);
        };
    }

    @Bean("sendPrivateMessageTool")
    @Description("Sends a private message (DM) to a user using their ID.")
    public Function<SendDMRequest, String> sendPrivateMessageTool(UserService service) {
        return req -> service.sendPrivateMessage(req.userId(), req.message());
    }

    @Bean("editPrivateMessageTool")
    @Description("Edits a previously sent private message (DM).")
    public Function<EditDMRequest, String> editPrivateMessageTool(UserService service) {
        return req -> service.editPrivateMessage(req.userId(), req.messageId(), req.newMessage());
    }

    @Bean("deletePrivateMessageTool")
    @Description("Deletes a private message (DM).")
    public Function<DeleteDMRequest, String> deletePrivateMessageTool(UserService service) {
        return req -> service.deletePrivateMessage(req.userId(), req.messageId());
    }

    @Bean("readPrivateMessagesTool")
    @Description("Reads history of private conversation (DMs) with a user.")
    public Function<ReadDMsRequest, String> readPrivateMessagesTool(UserService service) {
        return req -> service.readPrivateMessages(req.userId(), req.count());
    }

    @Bean("kickUserTool")
    @Description("Kicks a user from the server. They can rejoin if they have a new invite.")
    public Function<KickUserRequest, String> kickUserTool(UserService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.kickUser(guildId, req.userId(), req.reason());
        };
    }

    @Bean("banUserTool")
    @Description("Permanently bans a user from the server.")
    public Function<BanUserRequest, String> banUserTool(UserService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.banUser(guildId, req.userId(), req.reason());
        };
    }

    @Bean("timeoutUserTool")
    @Description("Mutes (Times Out) a user for a specified duration in minutes.")
    public Function<TimeoutUserRequest, String> timeoutUserTool(UserService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.timeoutUser(guildId, req.userId(), req.durationMinutes(), req.reason());
        };
    }

    @Bean("removeTimeoutTool")
    @Description("Removes the Timeout (Unmutes) from a user.")
    public Function<UnmuteUserRequest, String> removeTimeoutTool(UserService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            return service.removeTimeout(guildId, req.userId(), req.reason());
        };
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: WEBHOOKI (WebhookService)
    // ----------------------------------------------------------------------------------

    @Bean("createWebhookTool")
    @Description("Creates a new webhook in a specific channel.")
    public Function<CreateWebhookRequest, String> createWebhookTool(WebhookService service) {
        return req -> service.createWebhook(req.channelId(), req.name());
    }

    @Bean("deleteWebhookTool")
    @Description("Deletes a webhook using its ID.")
    public Function<DeleteWebhookRequest, String> deleteWebhookTool(WebhookService service) {
        return req -> service.deleteWebhook(req.webhookId());
    }

    @Bean("listWebhooksTool")
    @Description("Lists all webhooks in a specific channel (returns IDs and URLs).")
    public Function<ListWebhooksRequest, String> listWebhooksTool(WebhookService service) {
        return req -> service.listWebhooks(req.channelId());
    }

    @Bean("sendWebhookMessageTool")
    @Description("Sends a message via webhook using webhook ID.")
    public Function<SendWebhookMsgRequest, String> sendWebhookMessageTool(WebhookService service) {
        return req -> service.sendWebhookMessage(req.webhookId(), req.message());
    }

    // ----------------------------------------------------------------------------------
    // NARZĘDZIA: DISCORD ACTIVITIES
    // ----------------------------------------------------------------------------------

    @Bean("startDiscordActivityTool")
    @Description("Starts a Discord Activity (game) like Wordle, Poker, or YouTube. Returns an invite link.")
    public Function<StartActivityRequest, String> startDiscordActivityTool(ActivityService service) {
        return req -> {
            String guildId = BotContext.getGuildId();
            String userId = BotContext.getUserId();
            return service.createActivityInvite(guildId, userId, req.gameName());
        };
    }
}