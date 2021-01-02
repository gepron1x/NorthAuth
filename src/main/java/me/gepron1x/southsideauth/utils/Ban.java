package me.gepron1x.southsideauth.utils;

import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.dualfactor.VkProfile;
import me.gepron1x.southsideauth.status.VerificationStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Ban {
    private static Map<String, Ban> bannedIps = new HashMap<>();
    private static final SouthSideAuth plugin = SouthSideAuth.getInstance();
    private ScheduledTask unbanTask;
    private String reason;
    public Ban(ProxiedPlayer player, String reason, long period, TimeUnit time) {
        this.reason = reason;
        String adress = formatAdress(player.getSocketAddress());
        player.disconnect(new TextComponent(reason));
        VkProfile vkProfile = VerificationStatus.getVkProfile(player.getUniqueId());
        if(vkProfile != null) {
            vkProfile.sendMessage("На ваш аккаунт с ip "+adress+" было произвдена неудачная попытка входа. " +
                    "Вход с этого Ip заблокирован в целях безопасности.");
        }
        bannedIps.put(adress, this);
        this.unbanTask = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            bannedIps.remove(adress);
        }, period, time);
    }
    public TextComponent getReason() {
        return new TextComponent(reason);
    }
    public static Ban getBan(SocketAddress adress) {
        return bannedIps.get(formatAdress(adress));
    }

    private static String formatAdress(SocketAddress a) {
        return a.toString().split(":")[0];
    }
}
