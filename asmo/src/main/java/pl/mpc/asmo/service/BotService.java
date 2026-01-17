package pl.mpc.asmo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mpc.asmo.model.Prompt;
import pl.mpc.asmo.model.SubscribeType;

@Service
public class BotService {

    private final UserServiceDb userService;
    private final ChatClient chatClient;

    @Autowired
    public BotService(UserServiceDb userService, ChatClient chatClient) {
        this.userService = userService;
        this.chatClient = chatClient;
    }


    public String processPrompt(Prompt prompt) {
        if (prompt.discordServerId() == null) {
            throw new IllegalArgumentException("Discord Server ID (Guild ID) jest wymagane do wykonania operacji.");
        }

        String guildIdStr = prompt.discordServerId().toString();
        String userIdStr = prompt.discordcUserId().toString();

        //SubscribeType subscribeType = userService.getUserSubscription(prompt.discordcUserId());

        return chatClient.prompt()
                .system(sys -> sys.param("current_guild_id", guildIdStr)
                        .param("current_user_id", userIdStr))
                .user(prompt.prompt())
                .call()
                .content();
    }

}
