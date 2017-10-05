package net.latipay.ui.test.common;

import java.util.Random;

/**
 * @author jasonlu 7:11:15 PM
 */
public class RandomUtils {

    private static final String NUM      = "0123456789";
    private static final String CHAR     = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_NUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM   = new Random();

    public static String randomNum(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(NUM.charAt(RANDOM.nextInt(NUM.length())));
        }
        return sb.toString();
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR.charAt(RANDOM.nextInt(CHAR.length())));
        }
        return sb.toString();
    }

    public static String randomStringNum(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_NUM.charAt(RANDOM.nextInt(CHAR_NUM.length())));
        }
        return sb.toString();
    }

    public static String randomEmail() {
        return randomEmail(15);
    }

    public static String randomEmail(int length) {
        if (length < 5) throw new RuntimeException("random email length can't less than 5");
        int split = (length - 2) / 3;
        int add = (length - 2) % 3;
        return randomStringNum(split + add) + "@" + randomString(split) + "." + randomString(split);
    }
    
    public static String randomAmount() {
        String amount = RandomUtils.randomNum(3) + "." + RandomUtils.randomNum(2);
        while (amount.startsWith("0")) {
            amount = amount.replaceFirst("0", "");
        }
        return amount;
    }

}
