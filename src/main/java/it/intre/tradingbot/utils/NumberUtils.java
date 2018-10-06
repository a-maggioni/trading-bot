package it.intre.tradingbot.utils;

import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class NumberUtils {

    private NumberUtils() {
    }

    public static PrecisionNum toPrecisionNum(final BigDecimal number) {
        return PrecisionNum.valueOf(number);
    }

}
