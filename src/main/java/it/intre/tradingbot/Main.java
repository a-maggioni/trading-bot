package it.intre.tradingbot;

import it.intre.messagedispatcher.consumer.Consumer;
import it.intre.messagedispatcher.consumer.KafkaConsumer;
import it.intre.messagedispatcher.model.KafkaConfiguration;
import it.intre.messagedispatcher.model.KafkaRecord;
import it.intre.messagedispatcher.model.Record;
import it.intre.messagedispatcher.producer.KafkaProducer;
import it.intre.messagedispatcher.producer.Producer;
import it.intre.tradingbot.bot.Bot;
import it.intre.tradingbot.bot.EmaBot;
import it.intre.tradingbot.common.Constants;
import it.intre.tradingbot.model.BotConfiguration;
import it.intre.tradingbot.model.BotType;
import it.intre.tradingbot.model.Order;
import it.intre.tradingbot.model.Quote;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ParseException {
        CommandLine commandLine = getCommandLine(args);
        final String host = commandLine.getOptionValue("host");
        final String port = commandLine.getOptionValue("port");
        final int type = ((Number) commandLine.getParsedOptionValue("type")).intValue();
        final BotType botType = BotType.values()[type];
        final int barTimeFrame = ((Number) commandLine.getParsedOptionValue("barTimeFrame")).intValue();
        final int strategyTimeFrame = ((Number) commandLine.getParsedOptionValue("strategyTimeFrame")).intValue();
        final BigDecimal maxAmount = new BigDecimal((commandLine.getParsedOptionValue("maxAmount")).toString());
        final BotConfiguration botConfiguration = new BotConfiguration(botType, barTimeFrame, strategyTimeFrame, maxAmount);

        Map<String, Bot> botMap = new HashMap<>();
        KafkaConfiguration inputConfiguration = new KafkaConfiguration(host, port, Constants.GROUP_ID, Constants.CLIENT_ID, Constants.INPUT_TOPIC);
        Consumer consumer = new KafkaConsumer<>(inputConfiguration, String.class, Quote.class);
        KafkaConfiguration outputConfiguration = new KafkaConfiguration(host, port, Constants.GROUP_ID, Constants.CLIENT_ID, Constants.OUTPUT_TOPIC);
        Producer producer = new KafkaProducer<String, Quote>(outputConfiguration);

        while (true) {
            List<Record<String, Quote>> quotesRecords = consumer.receive();
            for (Record<String, Quote> quoteRecord : quotesRecords) {
                String symbol = quoteRecord.getKey();
                Quote quote = quoteRecord.getValue();
                if (!botMap.containsKey(symbol)) {
                    botMap.put(symbol, getBot(botConfiguration));
                }
                Bot bot = botMap.get(symbol);
                Order order = bot.executeStrategy(quote);
                if (order != null) {
                    Record orderRecord = new KafkaRecord<>(Constants.OUTPUT_TOPIC, order.getSymbol(), order);
                    boolean success = producer.send(orderRecord);
                    if (success) {
                        logger.debug("Sent order: {}", order);
                    }
                }
            }
            consumer.commit();
        }
    }

    private static Bot getBot(BotConfiguration botConfiguration) {
        return new EmaBot(botConfiguration);
    }

    private static CommandLine getCommandLine(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        Options options = getOptions();
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            helpFormatter.printHelp("TradingBot", options);
            System.exit(1);
        }
        return commandLine;
    }

    private static Options getOptions() {
        Options options = new Options();
        Option host = new Option("h", "host", true, "Kafka host");
        host.setRequired(true);
        options.addOption(host);
        Option port = new Option("p", "port", true, "Kafka port");
        port.setRequired(true);
        options.addOption(port);
        Option type = new Option("t", "type", true, "Bot type");
        type.setType(Number.class);
        type.setRequired(true);
        options.addOption(type);
        Option barTimeFrame = new Option("btf", "barTimeFrame", true, "Bar time frame (minutes)");
        barTimeFrame.setType(Number.class);
        barTimeFrame.setRequired(true);
        options.addOption(barTimeFrame);
        Option strategyTimeFrame = new Option("stf", "strategyTimeFrame", true, "Strategy time frame");
        strategyTimeFrame.setType(Number.class);
        strategyTimeFrame.setRequired(true);
        options.addOption(strategyTimeFrame);
        Option maxAmount = new Option("ma", "maxAmount", true, "Max amount (per order)");
        maxAmount.setType(Number.class);
        maxAmount.setRequired(true);
        options.addOption(maxAmount);
        return options;
    }

}
