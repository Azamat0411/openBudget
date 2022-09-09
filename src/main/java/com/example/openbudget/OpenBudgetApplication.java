package com.example.openbudget;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class OpenBudgetApplication {
    public static void main(String[] args){

        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(new BotController());
        }catch (TelegramApiException e){
            System.out.println(e);
        }
    }

}
