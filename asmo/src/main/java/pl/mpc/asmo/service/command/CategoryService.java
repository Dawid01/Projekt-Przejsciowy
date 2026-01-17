package pl.mpc.asmo.service.command;

import pl.mpc.asmo.annotation.RequiresBotAccess;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final JDA jda;

    @Value("${DISCORD_GUILD_ID:}")
    private String defaultGuildId;

    public CategoryService(@Lazy JDA jda) {
        this.jda = jda;
    }


    private String resolveGuildId(String guildId) {
        if ((guildId == null || guildId.isEmpty()) && defaultGuildId != null && !defaultGuildId.isEmpty()) {
            return defaultGuildId;
        }
        return guildId;
    }


    private Guild getGuild(String guildId) {
        String resolvedId = resolveGuildId(guildId);
        if (resolvedId == null || resolvedId.isEmpty()) {
            throw new IllegalArgumentException("guildId cannot be null");
        }
        Guild guild = jda.getGuildById(resolvedId);
        if (guild == null) {
            throw new IllegalArgumentException("Discord server not found by guildId: " + resolvedId);
        }
        return guild;
    }
    @RequiresBotAccess
    public String createCategory(String guildId, String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        Guild guild = getGuild(guildId);
        Category category = guild.createCategory(name).complete();
        return "Created new category: " + category.getName();
    }
    @RequiresBotAccess
    public String deleteCategory(String guildId, String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            throw new IllegalArgumentException("categoryId cannot be null");
        }
        Guild guild = getGuild(guildId);
        Category category = guild.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found by categoryId: " + categoryId);
        }
        String name = category.getName();
        category.delete().queue();
        return "Deleted category: " + name;
    }
    @RequiresBotAccess
    public String findCategory(String guildId, String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new IllegalArgumentException("categoryName cannot be null");
        }
        Guild guild = getGuild(guildId);
        List<Category> categories = guild.getCategoriesByName(categoryName, true);

        if (categories.isEmpty()) {
            throw new IllegalArgumentException("Category " + categoryName + " not found");
        }
        if (categories.size() > 1) {
            String list = categories.stream()
                    .map(c -> c.getName() + " (ID: " + c.getId() + ")")
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Multiple categories found: " + list + ". Please specify ID.");
        }
        Category category = categories.get(0);
        return "Retrieved category: " + category.getName() + ", ID: " + category.getId();
    }
    @RequiresBotAccess
    public String listChannelsInCategory(String guildId, String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            throw new IllegalArgumentException("categoryId cannot be null");
        }
        Guild guild = getGuild(guildId);
        Category category = guild.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found by categoryId");
        }

        List<GuildChannel> channels = category.getChannels();
        if (channels.isEmpty()) {
            return "Category " + category.getName() + " is empty.";
        }

        return "Channels in " + category.getName() + ":\n" +
                channels.stream()
                        .map(c -> "- " + c.getType() + ": " + c.getName() + " (ID: " + c.getId() + ")")
                        .collect(Collectors.joining("\n"));
    }
}