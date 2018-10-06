package it.intre.tradingbot.common;

import it.intre.tradingbot.model.Quote;
import it.intre.tradingbot.utils.DateUtils;
import it.intre.tradingbot.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.PrecisionNum;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class BarBuilder {

    private final Logger logger = LogManager.getLogger(BarBuilder.class);

    private int timeFrame;
    private BigDecimal open, high, low, close, firstVolume, lastVolume;
    private Long lastTimestamp;

    public BarBuilder(int timeFrame) {
        this.timeFrame = timeFrame;
    }

    public Bar getBar(final Quote quote) {
        Bar bar = null;

        if (this.lastTimestamp == null) {
            // first bar
            this.init(quote);
        } else if (DateUtils.getMinuteDifference(this.lastTimestamp, quote.getTimestamp()) >= this.timeFrame) {
            // bar completed
            this.logger.debug("Building bar for {}...", quote.getSymbol());
            bar = this.buildBar();
            this.logger.debug("Built bar: {}", bar);
            this.init(quote);
        } else {
            // same bar
            this.update(quote);
        }
        this.lastTimestamp = quote.getTimestamp();

        return bar;
    }

    private void init(final Quote quote) {
        this.logger.trace("Initializing bar for {}...", quote.getSymbol());
        this.open = quote.getPrice();
        this.high = quote.getPrice();
        this.low = quote.getPrice();
        this.close = quote.getPrice();
        this.firstVolume = quote.getVolume();
    }

    private void update(final Quote quote) {
        this.logger.trace("Updating bar for {}...", quote.getSymbol());
        BigDecimal price = quote.getPrice();
        this.high = price.compareTo(this.high) > 0 ? price : this.high;
        this.low = price.compareTo(this.low) < 0 ? price : this.low;
        this.close = price;
        this.lastVolume = quote.getVolume();
    }

    private Bar buildBar() {
        ZonedDateTime endTime = DateUtils.toZonedDateTime(this.lastTimestamp);
        PrecisionNum open = NumberUtils.toPrecisionNum(this.open);
        PrecisionNum high = NumberUtils.toPrecisionNum(this.high);
        PrecisionNum low = NumberUtils.toPrecisionNum(this.low);
        PrecisionNum close = NumberUtils.toPrecisionNum(this.close);
        PrecisionNum volume = this.lastVolume != null ?
                NumberUtils.toPrecisionNum(this.lastVolume.subtract(this.firstVolume)) :
                NumberUtils.toPrecisionNum(this.firstVolume);
        PrecisionNum amount = NumberUtils.toPrecisionNum(BigDecimal.ZERO);
        return new BaseBar(endTime, open, high, low, close, volume, amount);
    }
}
