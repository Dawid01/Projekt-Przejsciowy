package pl.mpc.asmo.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mpc.asmo.listener.SlashCommandListener;

@Configuration
public class DiscordConfig {

    @Value("${DISCORD_TOKEN}")
    private String token;

    @Bean
    public JDA jda(SlashCommandListener slashCommandListener) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(slashCommandListener)
                .build();

        jda.awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("asmo", "Wydaj polecenie do AI Asmo")
                        .addOption(OptionType.STRING, "prompt", "Treść polecenia lub pytania", true)
        ).queue();

        return jda;
    }
}