package me.gepron1x.southsideauth.utils.hashing;

import java.nio.charset.StandardCharsets;

public class Sha512 implements Hasher {
    @Override
    public String hash(String s) {
        return com.google.common.hash.Hashing.sha512().hashString(s, StandardCharsets.UTF_8).toString();
    }
}
