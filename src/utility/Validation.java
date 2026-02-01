package utility;

import java.math.BigDecimal;

public class Validation {
    public static boolean validPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 11) return false;
        for (char c : phoneNumber.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false; // Found a non-digit character (like an alphabet, space, or symbol)
            }
        }
        return true;
    }

    public static boolean validEmail(String email) {
        if (!email.contains("@")) return false;
        if (!email.contains(".")) return false;
        return email.indexOf("@") < email.lastIndexOf(".");
    }

    public static boolean validAmount(BigDecimal amount){
        return (amount.compareTo(BigDecimal.ZERO)>0);
    }


}
