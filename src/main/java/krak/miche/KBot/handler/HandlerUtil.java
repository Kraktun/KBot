package krak.miche.KBot.handler;

import java.util.Scanner;

/**
 * @author Kraktun
 * @version 1.0
 * Util class to manage commands and params
 */
public class HandlerUtil {

    public static final String LOGTAG = "HANDLERUTIL";

    /**
     * Extract the command from whole message
     * @param message message contained in a update
     * @return first argument of the message
     */
    public static String firstCommand(String message) {
        Scanner text = new Scanner(message);
        String temp = text.next();
        text.close();
        return temp;
    }

    /**
     * Extract parameters from whole message
     * @param message message contained in a update
     * @param paramNum number of parameters to extract, min is 1 (check that there are at least paramNum parameters must be done by the user
     * @return array of params (included command if present)
     */
    public static String[] paramCommand(String message, int paramNum) {
        Scanner text = new Scanner(message);
        //if present command is included
        String[] box = new String[paramNum];
        for (int i = 0; i < paramNum; i++)
        {
            box[i] = text.next();
        }
        text.close();
        return box;
    }

    /**
     * Count param number
     * @param message message contained in a update
     * @return number of parameters contained in the message
     */
    public static int getParamNum(String message) {
        Scanner text = new Scanner(message);
        //if present command is included
        int counter = 0;
        while (text.hasNext())
        {
            counter++;
            text.next();
        }
        text.close();
        return counter;
    }
}
