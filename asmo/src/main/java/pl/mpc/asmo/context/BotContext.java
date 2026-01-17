package pl.mpc.asmo.context;

public class BotContext {
    private static final ThreadLocal<String> currentGuildId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserName = new ThreadLocal<>();
    private static final ThreadLocal<String> currentChannelId = new ThreadLocal<>();

    // --- Settery ---
    public static void setGuildId(String guildId) {
        currentGuildId.set(guildId);
    }

    public static void setUserId(String userId) {
        currentUserId.set(userId);
    }

    public static void setUserName(String userName) {
        currentUserName.set(userName);
    }

    public static void setChannelId(String channelId) {
        currentChannelId.set(channelId);
    }

    // --- Gettery ---
    public static String getGuildId() {
        return currentGuildId.get();
    }

    public static String getUserId() {
        return currentUserId.get();
    }

    public static String getUserName() {
        return currentUserName.get();
    }

    public static String getChannelId() {
        return currentChannelId.get();
    }

    public static void clear() {
        currentGuildId.remove();
        currentUserId.remove();
        currentUserName.remove();
        currentChannelId.remove();
    }
}