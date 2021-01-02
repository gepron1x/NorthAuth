package me.gepron1x.southsideauth.dualfactor;

import me.gepron1x.southsideauth.SouthSideAuth;

public class VkProfile {
    private static final VkBot bot = SouthSideAuth.getInstance().getVkBot();
    private int vkID;
    private String code;
    public VkProfile(int vkID) {
        this.vkID = vkID;
    }

    public void sendCode() {
        this.code = VkBot.generateRandomCode();
        bot.sendCodeMessage(vkID, code);
    }
    public boolean checkCode(String input) {
       return input.equals(code);

    }
    public void sendMessage(String message) {
        bot.sendSimpleMessage(vkID, message);
    }
    public void resetCode() {
        this.code = null;
    }
}
