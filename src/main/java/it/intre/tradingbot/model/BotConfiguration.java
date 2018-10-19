package it.intre.tradingbot.model;

import java.math.BigDecimal;

public class BotConfiguration {

    private BotType botType;
    private String timeSeriesName;
    private int timeFrame;
    private BigDecimal maxAmount;

    public BotConfiguration(final BotType botType, final int timeFrame, final BigDecimal maxAmount) {
        this.botType = botType;
        this.timeSeriesName = botType + "TimeSeries";
        this.timeFrame = timeFrame;
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

    public int getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(int timeFrame) {
        this.timeFrame = timeFrame;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
}
