package me.gepron1x.southsideauth.dualfactor;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.storage.Storage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import ru.codeoff.bots.sdk.clients.Group;
import ru.codeoff.bots.sdk.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class VkBot {
 private Group group;
 private Map<Integer, DualFactorVerification> codes = new HashMap<>();
 private SouthSideAuth plugin;
 private Storage storage;
 private Configuration messages;

 private static final String pattern = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
 public VkBot(SouthSideAuth plugin, int groupID, String acessToken, Configuration messages) {
            this.messages = messages;
            this.plugin = plugin;
            this.storage = plugin.getStorage();
            group = new Group(groupID, acessToken);
            group.onCommand("/привязать", message -> {
                Configuration codeSend = messages.getSection("code-send");
                String nickname = message.getText().split(" ")[1];
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nickname);
                if(player == null) {
                    sendSimpleMessage(message.authorId(), codeSend.getString("player-not-exists"));
                } else {
                    String code = generateRandomCode();
                    codes.put(message.authorId(), new DualFactorVerification(code, player));
                    player.sendMessage(new TextComponent(
                            codeSend.getString("server")
                            .replaceAll("%code%", code)));
                    sendSimpleMessage(message.authorId(), codeSend.getString("bot"));
                }
            });
            group.onMessage((message) -> {
                int authorID = message.authorId();
                DualFactorVerification session = codes.get(authorID);
                if(session == null) return;
                if(session.checkCode(message.getText())) {
                    sendSimpleMessage(authorID, messages.getString("hook-success"));
                    UUID uuid = session.getPlayer().getUniqueId();
                    AuthProfile profile = AuthProfile.byUUID(uuid);
                    profile.set2FA(true);
                    storage.createDualFactor(uuid, authorID);
                    codes.remove(authorID);
                }
            });
 }

 public void sendSimpleMessage(int recieverID, String msg) {
     new Message().from(group).to(recieverID).text(msg).send();
 }
 public void sendCodeMessage(int recieverID, String code) {
     sendSimpleMessage(recieverID, messages.getString("verification").replace("%code%", code));
 }
 public static String generateRandomCode() {
     StringBuilder sb = new StringBuilder();
     Random r = new Random();
     for(int i = 0; i < 5; i++) {
         sb.append(pattern.toCharArray()[r.nextInt(pattern.length())]);
     }
     return sb.toString();

 }
}
