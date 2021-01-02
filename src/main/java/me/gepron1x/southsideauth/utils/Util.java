package me.gepron1x.southsideauth.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;

public final class Util {
    private Util() {
        //such empty
    }

    public static String paint(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void resetPlayerTitle(ProxiedPlayer player) {
        ProxyServer.getInstance().createTitle()
                .title(new TextComponent(""))
                .subTitle(new TextComponent(""))
                .fadeIn(0).fadeOut(0).send(player);
    }

    public static long parseTime(String s, TimeUnit unit) {
        char[] b = s.toCharArray();
        long summ = 0;
        while (s != "") {
            for (int i = 0; i < b.length; i++) {
                char c = b[i];
                int multiplyer;
                switch (c) {
                    case 's':
                        multiplyer = 1;
                        break;
                    case 'm':
                        multiplyer = 60;
                        break;
                    case 'h':
                        multiplyer = 3600;
                        break;
                    default:
                        continue;
                }
                int numbers = Integer.valueOf(s.substring(0, i - 1));
                summ += numbers * multiplyer;
                s = s.substring(i);
                break;
            }
        }
        return unit.convert(summ, TimeUnit.SECONDS);
    }
    public static <T> T getValue(Configuration cfg, String path, T def) {
        Configuration section = cfg;
        String[] keys = path.split(".");
        for(int i = 0; i < keys.length - 1; i++) {
            section = section.getSection(keys[i]);
        }
        return section.get(path, def);
    }

}
