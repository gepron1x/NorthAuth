package me.gepron1x.southsideauth.storage;

import me.gepron1x.southsideauth.AuthProfile;

import java.net.SocketAddress;
import java.util.UUID;

public interface Storage {
    void loadProfiles();
    void addSession(UUID uuid, SocketAddress adress);
    void removeSession(UUID uuid);
    void createProfile(UUID uuid, AuthProfile pl);
    void removeProfile(UUID uuid);
    void updatePassword(UUID uuid, String password);
    void createDualFactor(UUID uuid, int vkID);
    void removeDualFactor(UUID uuid);
}
