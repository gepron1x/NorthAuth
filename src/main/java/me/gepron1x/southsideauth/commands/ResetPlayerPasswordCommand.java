package me.gepron1x.southsideauth.commands;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.SouthSideAuth;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ResetPlayerPasswordCommand extends Command {
    private final SouthSideAuth plugin;
    public ResetPlayerPasswordCommand(SouthSideAuth plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender != ProxyServer.getInstance().getConsole()) return;
        UUID uuid = ProxyServer.getInstance().getPlayer(args[0]).getUniqueId();
        if(uuid == null) {
            plugin.send("§4Ошибка! Из-за особенностей строения плагина игрок должен находиться на сервере.");
            return;
        }
        String newPassword = plugin.getHasher().hash(args[1]);
        AuthProfile.byUUID(uuid).setHashedPassword(newPassword);
        plugin.getStorage().updatePassword(uuid, newPassword);
        plugin.send("§6Пароль игрока "+args[0]+ " успешно изменён.");

    }
}
