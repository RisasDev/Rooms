package dev.risas.nokrooms.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JavaUtil {

    public Integer getInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
