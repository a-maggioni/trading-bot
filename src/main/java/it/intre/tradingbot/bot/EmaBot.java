package it.intre.tradingbot.bot;

import it.intre.tradingbot.model.BotConfiguration;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

public class EmaBot extends Bot {

    public EmaBot(BotConfiguration botConfiguration) {
        super(botConfiguration);
    }

    @Override
    protected void buildStrategy() {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(this.timeSeries);
        EMAIndicator emaIndicator = new EMAIndicator(closePriceIndicator, this.strategyTimeFrame);
        this.strategy = new BaseStrategy(
                new CrossedUpIndicatorRule(emaIndicator, closePriceIndicator),
                new CrossedDownIndicatorRule(emaIndicator, closePriceIndicator)
        );
    }

}
