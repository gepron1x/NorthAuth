package me.gepron1x.southsideauth.utils.hashing;

public class None implements Hasher {
    @Override
    public String hash(String s) {
        return s;
    }
}
