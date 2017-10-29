package krak.miche.KBot.keyboards;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 */

public class satoolsKeyboard extends ReplyKeyboardMarkup {

    public satoolsKeyboard() {
    }

    public ReplyKeyboardMarkup sendCustomKeyboard() {
        //SendMessage message = new SendMessage();
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("List Groups");
        row.add("Export");
        row.add("List Users");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("Remove User");
        row.add("Change Status");
        row.add("Blacklist User");
        // Add the second row to the keyboard
        keyboard.add(row);
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("Get feedback");
        row.add("Clear Feedback");
        row.add("Shutdown");
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        // Add it to the message
        //message.setReplyMarkup(keyboardMarkup);
        return keyboardMarkup;
    }
}
