package it.intre.tradingbot.bot;

import it.intre.tradingbot.model.Bar;
import it.intre.tradingbot.model.BotConfiguration;
import it.intre.tradingbot.model.Order;
import it.intre.tradingbot.model.OrderType;
import it.intre.tradingbot.utils.DateUtils;
import it.intre.tradingbot.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.*;

import java.math.BigDecimal;

public abstract class Bot {

    protected final Logger logger = LogManager.getLogger(Bot.class);

    protected TimeSeries timeSeries;
    protected int timeFrame;
    protected Strategy strategy;
    private BigDecimal maxAmount;
    private TradingRecord tradingRecord;
    private Order lastEntry;
    private BigDecimal totalQuantity;
    private boolean firstBarDiscarded;

    protected Bot(BotConfiguration botConfiguration) {
        this.timeSeries = new BaseTimeSeries(botConfiguration.getTimeSeriesName());
        this.timeSeries.setMaximumBarCount(botConfiguration.getTimeFrame());
        this.timeFrame = botConfiguration.getTimeFrame();
        this.maxAmount = botConfiguration.getMaxAmount();
        this.tradingRecord = new BaseTradingRecord();
        this.totalQuantity = BigDecimal.ZERO;
        this.firstBarDiscarded = false;
    }

    protected abstract void buildStrategy();

    public Order executeStrategy(final Bar bar) {
        Order order = null;

        this.addBar(bar);
        if (this.strategy != null) {
            int endIndex = this.timeSeries.getEndIndex();
            if (this.strategy.shouldEnter(endIndex)) {
                order = this.enter(endIndex, bar);
            } else if (this.strategy.shouldExit(endIndex)) {
                order = this.exit(endIndex, bar);
            }
        }

        return order;
    }

    private void addBar(final Bar bar) {
        if (this.firstBarDiscarded) {
            this.logger.debug("Adding bar {}...", bar);
            this.timeSeries.addBar(
                    DateUtils.toZonedDateTime(bar.getEndTimestamp()),
                    NumberUtils.toPrecisionNum(bar.getOpen()),
                    NumberUtils.toPrecisionNum(bar.getHigh()),
                    NumberUtils.toPrecisionNum(bar.getLow()),
                    NumberUtils.toPrecisionNum(bar.getClose()),
                    NumberUtils.toPrecisionNum(bar.getVolume())
            );
        } else {
            // the first bar is incomplete and must be discarded
            this.logger.debug("Discarding first bar for {}", bar.getSymbol());
            this.firstBarDiscarded = true;
        }
        if (this.shouldBuildStrategy()) {
            this.logger.debug("Building strategy for {}...", bar.getSymbol());
            this.buildStrategy();
        }
    }

    private boolean shouldBuildStrategy() {
        return this.strategy == null &&
                this.timeSeries.getBarCount() == this.timeFrame;
    }

    private Order enter(final int index, final Bar bar) {
        Order order = null;
        BigDecimal price = bar.getClose();
        BigDecimal quantity = this.computeQuantity(price);
        boolean entered = this.tradingRecord.enter(
                index,
                NumberUtils.toPrecisionNum(price),
                NumberUtils.toPrecisionNum(quantity)
        );
        if (entered) {
            order = new Order(OrderType.BUY, bar.getSymbol(), price, quantity);
            this.afterEnter(order, quantity);
            this.logger.info("Entered: {}", order);
        }
        return order;
    }

    private BigDecimal computeQuantity(final BigDecimal price) {
        return this.maxAmount.divide(price, BigDecimal.ROUND_FLOOR);
    }

    private void afterEnter(final Order order, final BigDecimal quantity) {
        this.lastEntry = order;
        this.totalQuantity = this.totalQuantity.add(quantity);
    }

    private Order exit(final int index, final Bar bar) {
        Order order = null;
        BigDecimal price = bar.getClose();
        BigDecimal quantity = this.totalQuantity;
        boolean exited = this.tradingRecord.exit(
                index,
                NumberUtils.toPrecisionNum(price),
                NumberUtils.toPrecisionNum(quantity)
        );
        if (exited) {
            order = new Order(OrderType.SELL, bar.getSymbol(), price, quantity);
            this.afterExit(order, quantity);
            this.logger.info("Exited: {}", order);
        }
        return order;
    }

    private void afterExit(final Order order, final BigDecimal quantity) {
        BigDecimal profitLoss = order.getAmount().subtract(lastEntry.getAmount());
        order.setProfitLoss(profitLoss);
        this.totalQuantity = this.totalQuantity.subtract(quantity);
    }
}
