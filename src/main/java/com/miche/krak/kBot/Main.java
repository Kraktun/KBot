package com.miche.krak.kBot;

import com.miche.krak.kBot.bots.MainBot;
import com.miche.krak.kBot.utils.ManageHooksKt;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    public static void main(String[] args) {
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ManageHooksKt.onShutdown();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                //LOG
            }
        }));

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MainBot(new DefaultBotOptions()));
            ManageHooksKt.onStart();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
