package me.gepron1x.southsideauth.status;

import me.gepron1x.southsideauth.SouthSideAuth;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public enum Status {
    REGISTER(SouthSideAuth.getInstance().getStatus("register")),
    LOGIN(SouthSideAuth.getInstance().getStatus("login")),
    VERIFICATION(SouthSideAuth.getInstance().getStatus("2fa")),
    LOGGED_IN(null);
    private PlayerStatus status;
    Status(PlayerStatus status) {
        this.status = status;
    }
    public boolean send(ProxiedPlayer p, String msg) {
        if(status == null) return true;
        return status.send(p, msg);
    }
    public PlayerStatus get() {
        return status;
    }
}
