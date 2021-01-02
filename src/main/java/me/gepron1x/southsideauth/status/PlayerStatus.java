package me.gepron1x.southsideauth.status;

import static me.gepron1x.southsideauth.utils.Util.paint;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class PlayerStatus {



    public Title getTitleMessage() {
        return titleMessage;
    }

    private Title titleMessage;


    protected TextComponent wrongInputMessage;

    public PlayerStatus(Title titleMessage, String wrongInputMessage) {
        this.titleMessage = titleMessage;
        this.wrongInputMessage = new TextComponent(wrongInputMessage);
    }
    public abstract boolean send(ProxiedPlayer player, String message);


    public static Title generateTitle(String title, String subtitle) {
        Title t = ProxyServer.getInstance().createTitle();
        t.title(new TextComponent(paint(title)));
        t.subTitle(new TextComponent(paint(subtitle)));
        t.fadeIn(0);
        t.fadeOut(9999);
        return t;
    }



}

