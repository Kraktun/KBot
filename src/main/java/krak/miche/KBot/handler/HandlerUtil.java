package krak.miche.KBot.handler;

import java.util.Scanner;

public class HandlerUtil {

    public static final String LOGTAG = "HANDLERUTIL";

    //Extract the command from whole message
    public static String firstCommand(String frase) {
        Scanner text = new Scanner(frase);
        String temp = text.next();
        text.close();
        return temp;
    }

    //Extract parameters from whole message
    //Param num = number of parameters to extract, min is 1 (check that there are at least paramNum parameters must be done
    //by the user
    public static String[] paramCommand(String frase, int paramNum) {
        Scanner text = new Scanner(frase);
        //if present command is included
        String[] box = new String[paramNum];
        for (int i = 0; i < paramNum; i++)
        {
            box[i] = text.next();
        }
        text.close();
        return box;
    }

    //Count param number
    public static int getParamNum(String frase) {
        Scanner text = new Scanner(frase);
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
