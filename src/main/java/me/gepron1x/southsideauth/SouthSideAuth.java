package me.gepron1x.southsideauth;

import me.gepron1x.southsideauth.commands.ChangePassword;
import me.gepron1x.southsideauth.commands.ResetPlayerPasswordCommand;
import me.gepron1x.southsideauth.dualfactor.VkBot;
import me.gepron1x.southsideauth.events.Events;
import me.gepron1x.southsideauth.status.VerificationStatus;
import me.gepron1x.southsideauth.status.LoginStatus;
import me.gepron1x.southsideauth.status.PlayerStatus;
import me.gepron1x.southsideauth.status.RegisterStatus;
import me.gepron1x.southsideauth.storage.MySQL;
import me.gepron1x.southsideauth.storage.Storage;
import me.gepron1x.southsideauth.utils.config.Config;
import me.gepron1x.southsideauth.utils.config.Setting;
import me.gepron1x.southsideauth.utils.hashing.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SouthSideAuth extends Plugin {
    private Storage storage;
    private static SouthSideAuth instance;
    private VkBot vkBot;
    private Config config;
    private Config messages;
    private boolean vkBotEnabled;
    private Command changePasswordCommand, resetPlayerPasswordCommand;
    private Hasher hasher;
    private Map<String, PlayerStatus> statuses = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config");
        Configuration cfg = config.get();
        messages = new Config("messages");

        initStatuses();
        if(vkBotEnabled)
            send("ues");
        this.vkBot = new VkBot(
                this,
                Setting.VK_BOT_GROUP_ID.getInt(),
                Setting.VK_BOT_ACCESS_TOKEN.getString(),
                cfg.getSection("")
        );
        setupHasher(Setting.HASH.getString().toUpperCase(Locale.ROOT));
        this.storage = new MySQL(
                this,
                Setting.MYSQL_HOST.getString(),
                Setting.MYSQL_USERNAME.getString(),
                Setting.MYSQL_DATABASE.getString(),
                Setting.MYSQL_PASSWORD.getString()
        );
        getProxy().getPluginManager().registerListener(this, new Events(
                this,
                cfg.getSection("servers"),
                cfg.getShort("session-length")
                )
        );
        this.changePasswordCommand = new ChangePassword(this, "changepassword", cfg.getSection("commands").getSection("changepassword"));
        this.resetPlayerPasswordCommand = new ResetPlayerPasswordCommand(this, "resetpassword");
        getProxy().getPluginManager().registerCommand(this, resetPlayerPasswordCommand);
        getProxy().getPluginManager().registerCommand(this, changePasswordCommand);
    }

    @Override
    public void onDisable() {
        //Особо ничего нету...
    }
    private void initStatuses() {
        Configuration titles = config.get().getSection("messages");
        statuses.put("login", LoginStatus.fromConfig(titles.getSection("login")));

        Configuration passwordLength = config.get().getSection("password");
        statuses.put("register", RegisterStatus.fromConfig(titles.getSection("register"), passwordLength));
        statuses.put("2fa", VerificationStatus.fromConfig(titles.getSection("2fa")));
    }
    private void setupHasher(String name) {
        Hasher hs = null;
        switch(name) {
            case "CRC32":
                hs = new Crc32();
                break;
            case "SHA256":
                hs = new Sha256();
                break;
            case "SHA512":
                hs = new Sha512();
                break;
            case "NONE":
            default:
                hs = new None();
                break;
        }
        this.hasher = hs;

    }
    public PlayerStatus getStatus(String name) {
        return statuses.get(name);
    }
    public Configuration getConfig() {
        return config.get();
    }
    public Configuration getMessages() {
        return messages.get();
    }
    public Storage getStorage() {
        return storage;
    }
    public void send(String s) {
        getProxy().getConsole().sendMessage(new TextComponent("["+this.getDescription().getName() + "] " + s));
    }
    public Hasher getHasher() {return hasher; }
    public VkBot getVkBot() {
        return vkBot;
    }

    public static SouthSideAuth getInstance() {
        return instance;
    }

}
