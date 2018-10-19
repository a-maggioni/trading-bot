package it.intre.tradingbot.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateUtils {

    private DateUtils() {
    }

    public static ZonedDateTime toZonedDateTime(final Long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }

}
