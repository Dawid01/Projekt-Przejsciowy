package pl.mpc.asmo.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.mpc.asmo.exception.BotSecurityException;
import pl.mpc.asmo.model.User;
import pl.mpc.asmo.repository.UserRepository;

@Service
public class BotSecurityService {
    private final JDA jda;
    private final UserRepository userRepository;

    @Value("${app.subscription.link:https://saas.osabosa.pl}")
    private String subscriptionLink;

    public BotSecurityService(JDA jda , UserRepository userRepository) {
        this.jda = jda;
        this.userRepository = userRepository;
    }

    public void validateAccess(String guildId, String userId) {

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new BotSecurityException("Błąd: Nie znaleziono serwera (Guild ID: " + guildId + ").");
        }

        if (!guild.getOwnerId().equals(userId)) {
            throw new BotSecurityException("Brak dostępu: Tylko właściciel serwera może używać tych funkcji.");
        }


        User user = userRepository.findUserByDiscordId(userId)
                .orElseThrow(() -> new BotSecurityException("Nie masz konta w naszym systemie. Zarejestruj się na: " + subscriptionLink));



        if (!user.hasActiveSubscription()) {
            throw new BotSecurityException("💲 Twoja subskrypcja wygasła lub jest nieaktywna. \nAby korzystać z funkcji premium, odnów ją tutaj: " + subscriptionLink);
        }
    }
}
