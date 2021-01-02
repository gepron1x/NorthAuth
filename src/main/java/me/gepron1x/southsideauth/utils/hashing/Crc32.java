package me.gepron1x.southsideauth.utils.hashing;

import java.nio.charset.StandardCharsets;

public class Crc32 implements Hasher {
    @Override
    public String hash(String s) {
        return com.google.common.hash.Hashing.crc32().hashString(s, StandardCharsets.UTF_8).toString();
    }
}
