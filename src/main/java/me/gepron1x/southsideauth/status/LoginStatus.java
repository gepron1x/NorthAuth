package me.gepron1x.southsideauth.status;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.AuthSession;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.utils.Util;
import me.gepron1x.southsideauth.utils.hashing.Hasher;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.UUID;

public class LoginStatus extends PlayerStatus {
    private TextComponent successMessage;
    private LoginStatus(Title title, String wrongInputMessage, String successMessage) {
        super(title, wrongInputMessage);
        this.successMessage = new TextComponent(successMessage);
    }
    @Override
    public boolean send(ProxiedPlayer player, String message) {
        UUID uuid = player.getUniqueId();
        AuthProfile profile = AuthProfile.byUUID(uuid);
        Hasher hash = SouthSideAuth.getInstance().getHasher();
        if(profile.getHashedPassword().equalsIgnoreCase(hash.hash(message))) {
            player.sendMessage(successMessage);
            Status status = null;
            if(profile.isIs2FAEnabled()) {
                status = Status.VERIFICATION;
                ((VerificationStatus) status.get()).code(uuid);
            } else {
                status = Status.LOGGED_IN;
            }
            AuthSession.get(player.getSocketAddress()).setStatus(status);
            return true;
        } else {
            player.sendMessage(wrongInputMessage);
            AuthSession.get(player.getSocketAddress()).onWrongInput();
        }
        return false;
    }
    public static LoginStatus fromConfig(Configuration cfg) {
        return new LoginStatus(generateTitle(cfg.getString("title"), cfg.getString("subtitle")),
                Util.paint(cfg.getString("wrong-password")),
                Util.paint(cfg.getString("success")));
    }
}
