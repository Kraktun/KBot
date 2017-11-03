package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.services.Localizer;
import org.junit.jupiter.api.Test;

import static krak.miche.KBot.BuildVars.DEFAULT_LANG;
import static org.junit.jupiter.api.Assertions.*;

class TestBlacklistHandler {
    @Test
    void ErrorOnNullBlacklist() {
        String language = DEFAULT_LANG;
        StringBuilder test = new StringBuilder(Localizer.getString("syntax_error", language));
        assertEquals(test.toString(), BlacklistHandler.blacklist(null, language).toString());
    }

    @Test
    void ErrorOnEmptyBlacklist() {
        String language = DEFAULT_LANG;
        StringBuilder test = new StringBuilder(Localizer.getString("syntax_error", language));
        assertEquals(test.toString(), BlacklistHandler.blacklist("", language).toString());
    }

}