package krak.miche.KBot.keyboards;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 * Creates a custom keyboard for admins
 */

public class SatoolsKeyboard extends ReplyKeyboardMarkup {


    public static ReplyKeyboardMarkup sendCustomKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("List Groups");
        row.add("Export");
        row.add("List Users");
        keyboard.add(row);
        row = new KeyboardRow();
        row.add("Remove User");
        row.add("Change Status");
        row.add("Blacklist User");
        keyboard.add(row);
        row = new KeyboardRow();
        row.add("Get feedback");
        row.add("Clear Feedback");
        row.add("Shutdown");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }
}
