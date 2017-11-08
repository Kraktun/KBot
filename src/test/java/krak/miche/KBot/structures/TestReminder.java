package krak.miche.KBot.structures;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestReminder {

    @Test
    void FalseOnVoidTextType() {
        String type = "";
        String text = "";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnNullTextType() {
        String type = null;
        String text = null;
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnVoidTextT(){
        String type = "t";
        String text = "";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnVoidTextD() {
        String type = "d";
        String text = "";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnZeroTimer() {
        String type = "t";
        String text = "0.0.0.0";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnNegTimerDays() {
        String type = "t";
        String text = "-5.0.0.0";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnNegTimerHours() {
        String type = "t";
        String text = "0.-4.0.0";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnNegTimerMinutes() {
        String type = "t";
        String text = "0.0.-10.0";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnNegTimerSeconds() {
        String type = "t";
        String text = "0.0.0.-20";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }

    @Test
    void FalseOnZeroDate() {
        String type = "d";
        String text = "0.0.0.0";
        assertEquals(false, Reminder.isFormattedReminder(type, text));
    }
}
