package ru.yandex.practicum.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Утилиты для математических операций.
 */
public final class MathUtils {

    private MathUtils() {
        // Утилитный класс, не должен инстанцироваться
    }

    /**
     * Округлить значение до 2 знаков после запятой.
     */
    public static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
