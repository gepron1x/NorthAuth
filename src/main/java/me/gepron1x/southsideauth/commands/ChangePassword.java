package me.gepron1x.southsideauth.commands;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.storage.Storage;
import me.gepron1x.southsideauth.utils.Util;
import me.gepron1x.southsideauth.utils.hashing.Hasher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.UUID;

public class ChangePassword extends Command {
    private Storage storage;
    private TextComponent wrongOldPassword;
    private TextComponent success;

    public ChangePassword(SouthSideAuth plugin, String name, Configuration config) {
        super(name);
        this.wrongOldPassword = new TextComponent(Util.paint(config.getString("wrong-old-password")));
        this.success = new TextComponent(Util.paint(config.getString("success")));
        this.storage = plugin.getStorage();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer) || !sender.hasPermission("southsideauth.changepassword")) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID uuid = player.getUniqueId();
        AuthProfile profile = AuthProfile.byUUID(uuid);
        String oldPassword = args[0];
        String newPassword = args[1];
        Hasher hasher = SouthSideAuth.getInstance().getHasher();
        if(hasher.hash(oldPassword).equals(profile.getHashedPassword())) {
            String hashedPassword = hasher.hash(newPassword);
            profile.setHashedPassword(hashedPassword);
            storage.updatePassword(uuid, hashedPassword);
            player.sendMessage(success);
        } else {
            player.sendMessage(wrongOldPassword);
        }

    }
}
