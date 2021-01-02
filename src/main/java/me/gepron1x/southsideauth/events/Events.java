package me.gepron1x.southsideauth.events;


import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.AuthSession;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.utils.Ban;
import me.gepron1x.southsideauth.utils.Util;
import me.gepron1x.southsideauth.utils.hashing.TimerTask;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Events implements Listener {
    private ServerInfo authServer;
    private ServerInfo hubServer;

    private Map<SocketAddress, TimerTask> logoutTasks = new HashMap<>();
    private short logoutDelay;
    private TaskScheduler scheduler;
    private SouthSideAuth plugin;
    public Events(SouthSideAuth plugin, Configuration servers, short logoutDelay) {
        this.plugin = plugin;
        this.logoutDelay = logoutDelay;
        this.scheduler = plugin.getProxy().getScheduler();
        this.authServer = ProxyServer.getInstance().getServerInfo(servers.getString("auth"));
        this.hubServer = ProxyServer.getInstance().getServerInfo(servers.getString("hub"));

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();
        checkForBans(player);
        AuthProfile profile = AuthProfile.byUUID(player.getUniqueId());
        TimerTask logoutTask = logoutTasks.get(player.getSocketAddress());
        if(logoutTask != null) {
            logoutTask.cancel();
            logoutTasks.remove(player.getSocketAddress());
        }
        if(profile == null) {
            new AuthSession(player);
        }
           else if(!profile.isIpLoggedIn(player.getSocketAddress())) {
            new AuthSession(player, profile);

        } else {
            player.connect(hubServer);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerDisconnectEvent e) {
        ProxiedPlayer pl = e.getPlayer();
        Util.resetPlayerTitle(pl);
        UUID uuid = pl.getUniqueId();
        AuthProfile authP = AuthProfile.byUUID(uuid);
        if(!authP.isIpLoggedIn(pl.getSocketAddress()))
            return;

        SocketAddress adress = pl.getSocketAddress();
        logoutTasks.put(adress, new TimerTask(plugin, () -> {
            authP.logout(adress);
        }, 3, TimeUnit.HOURS));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        UUID uuid = player.getUniqueId();
        AuthProfile profile = AuthProfile.byUUID(uuid);
        if(profile.isIpLoggedIn(player.getSocketAddress())) return;
        e.setCancelled(true);
        AuthSession.get(player.getSocketAddress()).recieve(player, e.getMessage());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPermissionCheck(PermissionCheckEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
                ProxiedPlayer pl = (ProxiedPlayer) e.getSender();
                AuthProfile profile = AuthProfile.byUUID(pl.getUniqueId());
                if(profile == null || !profile.isIpLoggedIn(pl.getSocketAddress())) e.setHasPermission(false);
    }
    private void checkForBans(ProxiedPlayer p) {
        Ban ban = Ban.getBan(p.getSocketAddress());
        if(ban != null) {
            p.disconnect(ban.getReason());
        }
    }
}
