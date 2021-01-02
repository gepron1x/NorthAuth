package me.gepron1x.southsideauth;


import java.net.SocketAddress;
import java.util.*;

public class AuthProfile {
    private static Map<UUID, AuthProfile> accounts = new HashMap<>();

    private Set<String> loggedInIpSet = new HashSet<>();
    private String hashedPassword;
    private boolean is2FAEnabled;

    public AuthProfile() {
        is2FAEnabled = false;
    }
    public AuthProfile(String hashedPassword, boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
        this.hashedPassword = hashedPassword;
    }
    public void addAdress(SocketAddress ip) {
        this.loggedInIpSet.add(ip.toString().split(":")[0]);
    }
    public void addAdress(String ip) {
        this.loggedInIpSet.add(ip);
    }
    public void logout(SocketAddress ip) {
        this.loggedInIpSet.remove(ip.toString().split(":")[0]);
    }
    public boolean isIpLoggedIn(SocketAddress ip) {
        return loggedInIpSet.stream()
                .anyMatch(adress -> adress.equals(ip.toString().split(":")[0]));
    }



    public String getHashedPassword() {
        return hashedPassword;
    }


    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }
    public static void insert(UUID uuid, AuthProfile profile) {
        accounts.put(uuid, profile);
}
    public static AuthProfile byUUID(UUID uuid) {
        return accounts.get(uuid);
}

    public boolean isIs2FAEnabled() {
        return is2FAEnabled;
    }
    public void set2FA(boolean is) {
        this.is2FAEnabled = is;
    }
}
