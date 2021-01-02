package me.gepron1x.southsideauth.status;

import me.gepron1x.southsideauth.AuthSession;
import me.gepron1x.southsideauth.dualfactor.VkProfile;
import me.gepron1x.southsideauth.utils.Util;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VerificationStatus extends PlayerStatus {
    private static Map<UUID, VkProfile> profiles = new HashMap<>();
    private TextComponent successMessage;
    private VerificationStatus(Title title, String wrongInputMessage, String successMessage) {
        super(title, wrongInputMessage);
        this.successMessage = new TextComponent(successMessage);
    }

    @Override
    public boolean send(ProxiedPlayer player, String message) {
        UUID uuid = player.getUniqueId();
        VkProfile dProfile = profiles.get(uuid);
        if(dProfile.checkCode(message)) {
            player.sendMessage(successMessage);
            AuthSession.get(player.getSocketAddress()).setStatus(Status.LOGGED_IN);
            dProfile.resetCode();
            return true;
        }
        player.sendMessage(wrongInputMessage);
        AuthSession.get(player.getSocketAddress()).onWrongInput();
        return false;
    }
    public static VerificationStatus fromConfig(Configuration cfg) {
        return new VerificationStatus(generateTitle(cfg.getString("title"), cfg.getString("subtitle")),
                Util.paint(cfg.getString("wrong-code")),
                Util.paint(cfg.getString("success")));
    }
    public static void insert(UUID uuid, int vkID) {
        profiles.put(uuid, new VkProfile(vkID));
    }
    public void code(UUID uuid) {
        profiles.get(uuid).sendCode();
    }
    public static VkProfile getVkProfile(UUID uuid) {
        return profiles.get(uuid);
    }

}
