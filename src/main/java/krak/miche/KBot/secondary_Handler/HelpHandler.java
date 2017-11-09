package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.structures.CommandObject;
import krak.miche.KBot.structures.CommandsStatic;

import java.util.Comparator;
import java.util.List;

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
        List<CommandObject> commands = sortCommands(CommandsStatic.USER_COMMANDS);
        for (CommandObject command : commands)
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
        List<CommandObject> commands = sortCommands(CommandsStatic.GROUP_COMMANDS);
        for (CommandObject command : commands)
        {
            additionalCommands.append("\n").append(command.getCommand()).append(": ").append(command.getDescription());
        }
        return additionalCommands;
    }

    private static List<CommandObject> sortCommands(List<CommandObject> oldList) {
        oldList.sort(new Comparator<CommandObject>() {
            @Override
            public int compare(CommandObject o1, CommandObject o2) {
                return o1.getCommand().compareTo(o2.getCommand());
            }
        });
        return oldList;
    }
}
