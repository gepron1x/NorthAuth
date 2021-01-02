package me.gepron1x.southsideauth.status;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.AuthSession;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.utils.Util;
import me.gepron1x.southsideauth.utils.config.Setting;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.UUID;

public class RegisterStatus extends PlayerStatus {
    private String successMessage;
    private TextComponent tooShortPassword, tooLongPassword;
    public RegisterStatus(Title title, String successMessage) {
        super(title, "");
        this.successMessage = successMessage;
    }

    @Override
    public boolean send(ProxiedPlayer player, String message) {
        UUID uuid = player.getUniqueId();
        AuthProfile profile = AuthProfile.byUUID(uuid);

        int length = message.length();

        if(length < Setting.PASSWORD_MIN_LENGTH.getInt()) {
            player.sendMessage(tooShortPassword);
            return false;
        }
        else if(length > Setting.PASSWORD_MAX_LENGTH.getInt()) {
            player.sendMessage(tooLongPassword);
            return false;
        }

        profile.setHashedPassword(SouthSideAuth.getInstance().getHasher().hash(message));
        SouthSideAuth.getInstance().getStorage().createProfile(uuid, profile);
        player.sendMessage(new TextComponent(successMessage));
        AuthSession.get(player.getSocketAddress()).setStatus(Status.LOGGED_IN);
        return true;
    }
    public static RegisterStatus fromConfig(Configuration cfg, Configuration passwordLength) {
        RegisterStatus status = new RegisterStatus(
                generateTitle(cfg.getString("title"),
                        cfg.getString("subtitle")),
                Util.paint(cfg.getString("success"))
        );
        status.tooLongPassword = new TextComponent(Util.paint(passwordLength.getString("too-long-password")));
        status.tooShortPassword = new TextComponent(Util.paint(passwordLength.getString("too-short-password")));
        return status;
    }
}
