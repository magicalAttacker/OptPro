package com.example.optpro;

public class ConverterUtils {
    private static final String key = "01111001";
    public String decoder(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length()/2; i++) {
            String code = source.substring(i*2, (i+1)*2);
            result.append(decoderChar(code));
        }
        return result.toString();
    }
    private char decoderChar(String source) {
        int xor = Integer.valueOf(key, 2);
        int sou = Integer.valueOf(source, 16);
        int decoded = xor ^ sou;
        return (char) decoded;
    }
    public String encoder(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char code = source.charAt(i);
            result.append(encoderString(code));
        }
        return result.toString();
    }
    private String encoderString(char source) {
        int xor = Integer.valueOf(key, 2);
        int encoded = xor ^ source;
        return String.format("%02X", encoded);
    }
}