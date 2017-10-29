package krak.miche.KBot;

import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.handler.TestCommandsHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;


public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                UtilsMain.log("Received shutdown input");
                UtilsMain.onCloseJob();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    BotLogger.severe(LOGTAG, e);
                }
            }
        });
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }
        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                DefaultBotOptions options = new DefaultBotOptions();
                if (BuildVars.isMainBotEnabled)
                    telegramBotsApi.registerBot(new CommandsHandler(options, true, BuildVars.BOT_USERNAME));
                if (BuildVars.isTestBotEnabled)
                    telegramBotsApi.registerBot(new TestCommandsHandler(options, true, BuildVars.BOT_TEST_USERNAME));
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
            UtilsMain.onStartJob();
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
