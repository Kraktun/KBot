package krak.miche.KBot.structures;

import java.util.Scanner;

/**
 * @author Kraktun
 * @version 1.0
 */
public class CommandObject {

    private final String command;
    private final String description;
    private final boolean isFiltered;     //if it must be processed in processNonCommandUpdates()

    CommandObject(boolean isFiltered, String command, String description) {
        this.command = command;
        this.description = description;
        this.isFiltered = isFiltered;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getCommandOnly() {
        Scanner in = new Scanner(command);
        String toReturn = in.next();
        in.close();
        return toReturn;
    }

    public String getCommandWithUsername(String username) {
        Scanner in = new Scanner(command);
        String toReturn = in.next();
        in.close();
        return toReturn + "@" + username;
    }

    public boolean isFiltered() {
        return isFiltered;
    }


}
