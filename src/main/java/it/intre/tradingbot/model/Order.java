package it.intre.tradingbot.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {

    private OrderType type;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal amount;
    private Long timestamp;

    public Order() {
    }

    public Order(OrderType type, String symbol, BigDecimal price, BigDecimal quantity) {
        this.type = type;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.amount = price.multiply(quantity);
        if (type.equals(OrderType.BUY)) {
            this.amount = this.amount.negate();
        }
        this.timestamp = Instant.now().toEpochMilli();
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
