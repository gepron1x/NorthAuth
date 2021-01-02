package me.gepron1x.southsideauth;

import me.gepron1x.southsideauth.status.Status;
import me.gepron1x.southsideauth.utils.Ban;
import me.gepron1x.southsideauth.utils.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AuthSession {
    private static Map<SocketAddress, AuthSession> sessions = new HashMap<>();
    private static final SouthSideAuth plugin = SouthSideAuth.getInstance();
    private static ServerInfo hubServer = ProxyServer.getInstance().getServerInfo("lobby");
    private static ServerInfo authServer = ProxyServer.getInstance().getServerInfo("auth");
    private Status status;
    private ProxiedPlayer player;
    private AuthProfile profile;
    private short attempts = 5;
    private ScheduledTask kickTask;
    public AuthSession(ProxiedPlayer player, AuthProfile profile) {
        player.connect(authServer);
        this.profile = profile;
        this.status = Status.LOGIN;
        kickTask = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            player.disconnect(new TextComponent("Вы слишком долго думаете!"));
        }, 120, TimeUnit.SECONDS);
        this.player = player;
        status.get().getTitleMessage().send(player);
        sessions.put(player.getSocketAddress(), this);
    }
    public AuthSession(ProxiedPlayer player) {
        player.connect(authServer);
        this.profile = new AuthProfile();
        this.player = player;
        AuthProfile.insert(player.getUniqueId(), profile);
        this.status = Status.REGISTER;
        kickTask = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            player.disconnect(new TextComponent("Вы слишком долго думаете!"));
        }, 120, TimeUnit.SECONDS);
        status.get().getTitleMessage().send(player);
        sessions.put(player.getSocketAddress(), this);
    }
    public void recieve(ProxiedPlayer sender, String message) {
        if(status.send(sender, message)) {
            this.attempts = 5;
        } else if(attempts <= 0) {
            Util.resetPlayerTitle(player);
            new Ban(player, "Этот ip заблокирован за слишком большое кол-во неудачных попыток входа.", 15, TimeUnit.MINUTES);
        }

    }
    public void onWrongInput() {
        this.attempts -= 1;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status s) {
        this.status = s;
        if(s != Status.LOGGED_IN) s.get().getTitleMessage().send(player);
        if(status == Status.LOGGED_IN) {
            player.connect(hubServer);
            Util.resetPlayerTitle(player);
            SocketAddress ip = player.getSocketAddress();
            profile.addAdress(ip);
            kickTask.cancel();
            remove(ip);
        }
    }
    public static AuthSession get(SocketAddress ip) {
        return sessions.get(ip);
    }
    public static void remove(SocketAddress ip) {
        sessions.remove(ip);
    }

}
