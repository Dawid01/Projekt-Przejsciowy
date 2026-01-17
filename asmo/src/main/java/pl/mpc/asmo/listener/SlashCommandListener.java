package pl.mpc.asmo.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import pl.mpc.asmo.context.BotContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private final ChatClient chatClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SlashCommandListener(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("asmo")) return;

        var option = event.getOption("prompt");
        if (option == null) {
            event.reply("Błąd: Nie podano treści polecenia.").setEphemeral(true).queue();
            return;
        }
        String userPrompt = option.getAsString();

        String guildId = (event.getGuild() != null) ? event.getGuild().getId() : null;
        String userId = event.getUser().getId();
        String userName = event.getUser().getName();
        String channelId = event.getChannel().getId();

        event.deferReply().queue();

        executorService.submit(() -> {
            try {
                BotContext.setGuildId(guildId);
                BotContext.setUserId(userId);
                BotContext.setUserName(userName);
                BotContext.setChannelId(channelId);

                String aiResponse = chatClient.prompt()
                        .user(userPrompt)
                        .call()
                        .content();

                if (aiResponse.length() > 2000) {
                    event.getHook().editOriginal(aiResponse.substring(0, 1990) + "...").queue();
                } else {
                    event.getHook().editOriginal(aiResponse).queue();
                }

            } catch (Exception e) {
                event.getHook().editOriginal("Błąd: " + e.getMessage()).queue();
                e.printStackTrace();
            } finally {
                BotContext.clear();
            }
        });
    }
}