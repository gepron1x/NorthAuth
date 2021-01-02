package me.gepron1x.southsideauth.utils.config;

import me.gepron1x.southsideauth.SouthSideAuth;
import net.md_5.bungee.config.Configuration;
import static me.gepron1x.southsideauth.utils.Util.getValue;

import java.util.ArrayList;
import java.util.List;

public enum Setting {
    SESSION_LENGTH("session-length"),
    HASH("hash"),
    PASSWORD_MIN_LENGTH("password.min-length"),
    PASSWORD_MAX_LENGTH("password.max-length"),
    MYSQL_HOST("mysql.host"),
    MYSQL_USERNAME("mysql.user"),
    MYSQL_DATABASE("mysql.database"),
    MYSQL_PASSWORD("mysql.password"),
    VK_BOT_ENABLED("vkbot.enabled"),
    VK_BOT_GROUP_ID("vkbot.group-id"),
    VK_BOT_ACCESS_TOKEN("vkbot.access-token"),
    SERVER_HUB("servers.lobby"),
    SERVER_AUTH("servers.auth");




    private static final Configuration config = SouthSideAuth.getInstance().getConfig();
    private String path;
    Setting(String path) {
        this.path = path;
    }
    @Override
    public String toString() {
        return getValue(config, path, "");
    }
    public String getString() {
        return getValue(config, path, "");
    }
    public int getInt() {
        return getValue(config, path, 0);
    }
    public boolean getBoolean() {
        return getValue(config, path, false);
    }
    public List<String> getStringList() {
        return getValue(config, path, new ArrayList<String>());
    }

}

