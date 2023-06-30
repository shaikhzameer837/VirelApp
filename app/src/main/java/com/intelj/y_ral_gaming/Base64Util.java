package com.intelj.y_ral_gaming;

import android.util.Base64;
import java.util.regex.Pattern;

public class Base64Util {
    public static boolean isBase64(String string) {
        // Check if the string is empty or contains non-base64 characters
        if (string == null || string.isEmpty() || !Pattern.matches("^[a-zA-Z0-9+/]+={0,2}$", string)) {
            return false;
        }

        // Calculate the expected padding length
        int expectedPadding = string.length() % 4;
        if (expectedPadding == 1) {
            // Invalid padding
            return false;
        }

        // Decode the string
        try {
            byte[] decodedBytes = Base64.decode(string, Base64.DEFAULT);

            // Check if the decoded bytes can be encoded back to the original string
            String encodedString = Base64.encodeToString(decodedBytes, Base64.DEFAULT).trim();
            return string.equals(encodedString);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
