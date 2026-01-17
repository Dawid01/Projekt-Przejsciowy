package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ActivityService {

    private final JDA jda;

    private static final Map<String, String> ACTIVITIES = new HashMap<>();

    static {
        ACTIVITIES.put("youtube", "880218394199220334");     // Watch Together
        ACTIVITIES.put("poker", "755827907998023700");       // Poker Night
        ACTIVITIES.put("chess", "832012774040141894");       // Chess in the Park

        ACTIVITIES.put("sketchheads", "902271654783242291"); // Kalambury
        ACTIVITIES.put("wordle", "879863976006127627");      // Word Snacks
        ACTIVITIES.put("letterleague", "879863686565621790"); // Letter League
    }

    public ActivityService(JDA jda) {
        this.jda = jda;
    }

    @RequiresBotAccess
    public String createActivityInvite(String guildId, String userId, String activityName) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return "Błąd: Nie znaleziono serwera.";

        Member member = guild.retrieveMemberById(userId).complete();
        if (member == null) return "Błąd: Nie znaleziono użytkownika.";

        if (member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            return "Musisz być na kanale głosowym, aby uruchomić aktywność!";
        }

        VoiceChannel voiceChannel;
        try {
            voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
        } catch (Exception e) {
            return "Aktywności działają tylko na zwykłych kanałach głosowych.";
        }

        String appId = ACTIVITIES.get(activityName.toLowerCase());
        if (appId == null) {
            appId = ACTIVITIES.get("wordle");
        }

        try {
            return voiceChannel.createInvite()
                    .setTargetApplication(Long.parseLong(appId))
                    .setMaxAge(3600)
                    .setMaxUses(1)
                    .complete()
                    .getUrl();
        } catch (Exception e) {
            return "Nie udało się stworzyć aktywności. Błąd: " + e.getMessage();
        }
    }
}