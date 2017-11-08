package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.structures.CommandObject;
import krak.miche.KBot.structures.CommandsStatic;

/**
 * @author Kraktun
 * @version 1.0
 */
public class HelpHandler {

    private static final String LOGTAG = "HELPHANDLER";

    /**
     * Get a list of commands available for user chats
     * @return list of commands with description
     */
    public static StringBuilder getHelpUser() {
        StringBuilder additionalCommands = new StringBuilder();
        for (CommandObject command : CommandsStatic.USER_COMMANDS)
        {
            additionalCommands.append("\n").append(command.getCommand()).append(": ").append(command.getDescription());
        }
        return additionalCommands;
    }

    /**
     * Get a list of commands available for group chats
     * @return list of commands with description
     */
    public static StringBuilder getHelpGroup() {
        StringBuilder additionalCommands = new StringBuilder();
        for (CommandObject command : CommandsStatic.GROUP_COMMANDS)
        {
            additionalCommands.append("\n").append(command.getCommand()).append(": ").append(command.getDescription());
        }
        return additionalCommands;
    }
}
