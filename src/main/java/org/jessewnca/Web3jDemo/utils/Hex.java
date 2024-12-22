package org.jessewnca.Web3jDemo.utils;

import lombok.NoArgsConstructor;

/**
 * @author jesse.huang
 */
@NoArgsConstructor
public class Hex {

    public String formatHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte aByte = var3[var5];
            stringBuilder.append(this.byteToHex(aByte));
        }

        return stringBuilder.toString();
    }

    public static byte[] parseHex(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
        } else {
            byte[] bytes = new byte[hexString.length() / 2];

            for(int i = 0; i < hexString.length(); i += 2) {
                bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
            }

            return bytes;
        }
    }

    private String byteToHex(byte num) {
        char[] hexDigits = new char[]{Character.forDigit(num >> 4 & 15, 16), Character.forDigit(num & 15, 16)};
        return new String(hexDigits);
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte)((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
        } else {
            return digit;
        }
    }
}
