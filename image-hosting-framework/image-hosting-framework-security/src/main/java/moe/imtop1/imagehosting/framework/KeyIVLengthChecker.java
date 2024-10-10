package moe.imtop1.imagehosting.framework;

import java.util.Base64;

public class KeyIVLengthChecker {
    public static void main(String[] args) {
        String key = "giratubT2DPXVyqG9OeHBQ==";
        String iv = "LJbbtg6XdGAnEikIarLtGg==";

        byte[] decodedKey = Base64.getDecoder().decode(key);
        byte[] decodedIV = Base64.getDecoder().decode(iv);

        System.out.println("Key Length: " + decodedKey.length + " bytes" + "value:" + new String(decodedKey));
        System.out.println("IV Length: " + decodedIV.length + " bytes"+ "value:" + new String(decodedIV));
    }
}
