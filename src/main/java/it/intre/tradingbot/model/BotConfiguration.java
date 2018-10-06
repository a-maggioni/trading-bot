package it.intre.tradingbot.model;

import java.math.BigDecimal;

public class BotConfiguration {

    private BotType botType;
    private String timeSeriesName;
    private int barTimeFrame;
    private int strategyTimeFrame;
    private BigDecimal maxAmount;

    public BotConfiguration(final BotType botType, final int barTimeFrame, final int strategyTimeFrame, final BigDecimal maxAmount) {
        this.botType = botType;
        this.timeSeriesName = botType + "TimeSeries";
        this.barTimeFrame = barTimeFrame;
        this.strategyTimeFrame = strategyTimeFrame;
        this.maxAmount = maxAmount;
    }

    public BotType getBotType() {
        return botType;
    }

    public void setBotType(BotType botType) {
        this.botType = botType;
    }

    public String getTimeSeriesName() {
        return timeSeriesName;
    }

    public void setTimeSeriesName(String timeSeriesName) {
        this.timeSeriesName = timeSeriesName;
    }

    public int getBarTimeFrame() {
        return barTimeFrame;
    }

    public void setBarTimeFrame(int barTimeFrame) {
        this.barTimeFrame = barTimeFrame;
    }

    public int getStrategyTimeFrame() {
        return strategyTimeFrame;
    }

    public void setStrategyTimeFrame(int strategyTimeFrame) {
        this.strategyTimeFrame = strategyTimeFrame;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
}
