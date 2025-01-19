package com.erms.utils;

public class EnumUtils {

    public static <E extends Enum<E>> boolean isEnumName(String input, Class<E> enumClass) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equals(input)) {
                return true;
            }
        }
        return false;
    }
}
