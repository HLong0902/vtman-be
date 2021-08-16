package com.viettel.vtman.cms.infrastructure.util;

import java.security.SecureRandom;

public class RandomUtil {

    public static String generateRandomString(String prefix, int stringLength, int option) {

        // Store character for generate string
        String strAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String strCapitalAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String strDigits = "0123456789";

        // Store string to generate
        String strValid;

        switch (option) {
            case 1:
                strValid = strDigits;
                break;
            case 2:
                strValid = strCapitalAlphabet + strDigits;
                break;
            case 3:
                strValid = strCapitalAlphabet + strAlphabet + strDigits;
                break;
            default:
                strValid = strCapitalAlphabet + strAlphabet + strDigits;
                break;
        }

        String stringRandom = "";

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < stringLength; i++) {
            int randnum = random.nextInt(strValid.length());
            stringRandom = stringRandom + strValid.charAt(randnum);
        }

        stringRandom = prefix + stringRandom;

        return stringRandom;
    }
}
