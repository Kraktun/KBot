package com.miche.krak.kBot;

import com.miche.krak.kBot.bots.MainBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MainBot(new DefaultBotOptions()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
