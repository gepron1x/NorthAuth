package me.gepron1x.southsideauth.dualfactor;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DualFactorVerification {
    private String code;
    private ProxiedPlayer player;
    public DualFactorVerification(String code, ProxiedPlayer player) {
        this.player = player;
        this.code = code;
    }
    public boolean checkCode(String input) {
        return code.equals(input);
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }
}
