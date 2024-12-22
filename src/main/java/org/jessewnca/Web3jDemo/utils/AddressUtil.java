package org.jessewnca.Web3jDemo.utils;

import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author jesse.huang
 */

@NoArgsConstructor
public class AddressUtil {

    private static final String HEX_PREFIX = "0x";
    private static final char[] HEX_CHAR_MAP = "0123456789abcdef".toCharArray();
    private static final String EVM_HASH_PATTERN = "^0x[A-Fa-f0-9]{64}$";
    private static final String TRON_HASH_PATTERN = "^[A-Fa-f0-9]{64}$";
    private static final Pattern tronHashpattern = Pattern.compile("^[A-Fa-f0-9]{64}$");
    private static final Pattern evmHashPattern = Pattern.compile("^0x[A-Fa-f0-9]{64}$");
    private static final BigInteger MIN_TUSD_REDEMPTION_ADDRESS = new BigInteger("0000000000000000000000000000000000000001", 16);
    private static final BigInteger MAX_TUSD_REDEMPTION_ADDRESS = new BigInteger("00000000000000000000000000000000000fffff", 16);


    public static boolean isEvmTransactionHash(String hash) {
        return hash != null && evmHashPattern.matcher(hash).matches();
    }

    public static boolean isTronTransactionHash(String hash) {
        return hash != null && tronHashpattern.matcher(hash).matches();
    }

    public static boolean isEthAddress(String address) {
        String cleanInput = cleanHexPrefix(address);

        try {
            toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException var3) {
            return false;
        }

        return cleanInput.length() == 40;
    }

    public static boolean isTronAddress(String address) {
        try {
            String evmAddress = tronToEvmAddress(address);
            return isEthAddress("0x" + evmAddress);
        } catch (NoSuchAlgorithmException | IllegalArgumentException var2) {
            return false;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return input != null && input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    public static String tronToEvmAddress(String tronAddress) throws NoSuchAlgorithmException {
        if (tronAddress != null && tronAddress.startsWith("T") && tronAddress.length() == 34) {
            byte[] decoded = Base58.decode(tronAddress);
            if (decoded.length == 25 && decoded[0] == 65) {
                byte[] checkSum = Arrays.copyOfRange(decoded, 21, 25);
                byte[] h1 = Arrays.copyOfRange(decoded, 0, 21);
                byte[] h2 = sha256(h1);
                byte[] hash1 = sha256(h2);
                byte[] firstCheckSum = Arrays.copyOfRange(hash1, 0, 4);
                if (!Arrays.equals(checkSum, firstCheckSum)) {
                    throw new IllegalArgumentException("Invalid Tron address");
                } else {
                    byte[] evmAddress = new byte[20];
                    System.arraycopy(decoded, 1, evmAddress, 0, 20);
                    return toHexStringNoPrefix(evmAddress);
                }
            } else {
                throw new IllegalArgumentException("Invalid Tron address");
            }
        } else {
            throw new IllegalArgumentException("Invalid Tron address");
        }
    }

    public static String evmToTronAddress(String evmAddress) throws NoSuchAlgorithmException {
        if (evmAddress != null && evmAddress.startsWith("0x") && evmAddress.length() == 42) {
            evmAddress = evmAddress.replace("0x", "41");
            if (evmAddress.length() % 2 == 1) {
                evmAddress = "0" + evmAddress;
            }

            byte[] decoded = Hex.parseHex(evmAddress);
            if (decoded.length != 21) {
                throw new IllegalArgumentException("Invalid Evm address");
            } else {
                byte[] hash0 = sha256(decoded);
                byte[] hash1 = sha256(hash0);
                byte[] inputCheck = new byte[decoded.length + 4];
                System.arraycopy(decoded, 0, inputCheck, 0, decoded.length);
                System.arraycopy(hash1, 0, inputCheck, decoded.length, 4);
                return Base58.encode(inputCheck);
            }
        } else {
            throw new IllegalArgumentException("Invalid Evm address");
        }
    }

    private static String toHexStringNoPrefix(byte[] input) {
        return toHexString(input, 0, input.length, false);
    }

    private static String toHexStringNoPrefix(BigInteger value) {
        return value.toString(16);
    }

    private static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        String output = new String(toHexCharArray(input, offset, length));
        return withPrefix ? "0x" + output : output;
    }

    private static char[] toHexCharArray(byte[] input, int offset, int length) {
        char[] output = new char[length << 1];
        int i = offset;

        for(int j = 0; i < length + offset; ++j) {
            int v = input[i] & 255;
            output[j++] = HEX_CHAR_MAP[v >>> 4];
            output[j] = HEX_CHAR_MAP[v & 15];
            ++i;
        }

        return output;
    }

    private static String cleanHexPrefix(String input) {
        return containsHexPrefix(input) ? input.substring(2) : input;
    }

    private static BigInteger toBigIntNoPrefix(String hexValue) {
        return new BigInteger(hexValue, 16);
    }

    private static byte[] sha256(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input);
    }
}
