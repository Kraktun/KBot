package krak.miche.KBot.structures;

import krak.miche.KBot.BuildVars;

import java.util.ArrayList;
import java.util.List;

/**
 * Why do I use this class and not the ICommandRegistry? Because in this way I can choose different descriptions for the same command
 * if /help is sent in user chat or in a group. I can also include commands which do not extend BotCommand (and are not in ICommandRegistry).
 *
 */
/**
 * @author Kraktun
 * @version 1.0
 */
public class CommandsStatic {

    public static final List<CommandObject> GROUP_COMMANDS = new ArrayList<>();
    public static final List<CommandObject> USER_COMMANDS = new ArrayList<>();

    static {
        GROUP_COMMANDS.add(new CommandObject(true,"/kick", "Reply to a message with this command to remove that user from this group"));
        GROUP_COMMANDS.add(new CommandObject(true,"/setstatus + [STATUS]", "Reply to a message with this command to change that user's status where status = \n" + BuildVars.USER_STATUS + "\n" + BuildVars.POWER_USER_STATUS + "\n" + BuildVars.ADMIN_STATUS + "\n" + BuildVars.BLACKLISTED_STATUS));
        GROUP_COMMANDS.add(new CommandObject(false,"/getadmins", "Get a list of all the admins in the group"));
        GROUP_COMMANDS.add(new CommandObject(true,"/ban", "Reply to a message with this command to remove that user from this group and ban him"));
        GROUP_COMMANDS.add(new CommandObject(true,"/unban", "Reply to a message with this command to unban that user from this group.\nThis command can be used also as a normal message (no reply): /unban + [ID]\n where ID = id of the user to unban"));
        GROUP_COMMANDS.add(new CommandObject(false,"/info", "Returns the chat ID"));
        GROUP_COMMANDS.add(new CommandObject(false,"/antiflood + [MESS NUM] + [TIMESLICE]", "Set antiflood where MESS NUM = max number of messages in chosen SLICE of TIME"));
        GROUP_COMMANDS.add(new CommandObject(false,"/antiflood + start\\stop", "start\\stop antiflood with last settings (default if not set: 10, 5)"));
        GROUP_COMMANDS.add(new CommandObject(false,"/lang + en\\it", "set the language for the bot in this group"));
        GROUP_COMMANDS.add(new CommandObject(false,"/start", "reloads admins table (use this after you add\\remove a user to\\from admins list in telegram"));
        GROUP_COMMANDS.add(new CommandObject(false,"/help", "Get all the commands this bot provides"));
        GROUP_COMMANDS.add(new CommandObject(false,"/status", "Get your status in the group"));
        GROUP_COMMANDS.add(new CommandObject(false,"/hello", "Say hello to this bot"));
        GROUP_COMMANDS.add(new CommandObject(false,"/upduser", "Update Username for admins"));
        GROUP_COMMANDS.add(new CommandObject(false,"/log + start\\stop", "Use to log messages"));
        GROUP_COMMANDS.add(new CommandObject(false,"/log + default\\invert", "Change log type: default = only messages starting with '" + BuildVars.LOG_SPECIAL_CHAR + "' are saved; invert = the opposite"));
        GROUP_COMMANDS.add(new CommandObject(false,"/exportlog", "Use to save log messages as text"));
        GROUP_COMMANDS.add(new CommandObject(false,"/clearlog", "Deletes logs for selected group"));

        USER_COMMANDS.add(new CommandObject(false,"/blacklist", "This command can be used only by admins to blacklist users \n usage: \n command + [ID]"));
        USER_COMMANDS.add(new CommandObject(false,"/clearfeedback", "Delete all feedbacks"));
        USER_COMMANDS.add(new CommandObject(false,"/customSQL", "Execute a custom SQL command"));
        USER_COMMANDS.add(new CommandObject(false,"/download", "Download file (WIP)"));
        USER_COMMANDS.add(new CommandObject(false,"/export", "This command backups admins list usage: \ncommand + [file name]"));
        USER_COMMANDS.add(new CommandObject(false,"/feedback", "Leave a feedback"));
        USER_COMMANDS.add(new CommandObject(false,"/getadmins", "Get admins list"));
        USER_COMMANDS.add(new CommandObject(false,"/hello", "Say hello to this bot"));
        USER_COMMANDS.add(new CommandObject(false,"/help", "Get all the commands this bot provides"));
        USER_COMMANDS.add(new CommandObject(false,"/info", "This command returns the chat ID"));
        USER_COMMANDS.add(new CommandObject(false,"/initialize", "With this command you can initialize the database"));
        USER_COMMANDS.add(new CommandObject(false,"/lang", "Used to set bot language in groups"));
        USER_COMMANDS.add(new CommandObject(false,"/listusers", "This command can be used to list users"));
        USER_COMMANDS.add(new CommandObject(false,"/remindme", "Set a reminder"));
        USER_COMMANDS.add(new CommandObject(false,"/rmuser", "This command can be used only by admins to remove users from database \n usage: \n command + [ID]"));
        USER_COMMANDS.add(new CommandObject(false,"/getfeedback", "This command can be used to get feedbacks"));
        USER_COMMANDS.add(new CommandObject(false,"/satools", "Tools for admins"));
        USER_COMMANDS.add(new CommandObject(false,"/settings", "Used to define settings"));
        USER_COMMANDS.add(new CommandObject(false,"/setuser", "This command can be used only by admins to promote users \n usage: \n command + [ID] + [position] \n where position = \n[USER]\n[POWER_USER]\n[ADMIN]\n[BLACKLISTED]"));
        USER_COMMANDS.add(new CommandObject(false,"/setutc", "This command is used to set your UTC (default +1)"));
        //USER_COMMANDS.add(new CommandObject(false,"/shutdown", "Disconnects the database"));
        USER_COMMANDS.add(new CommandObject(false,"/start", "With this command you can start the Bot"));
        USER_COMMANDS.add(new CommandObject(false,"/status", "This command returns your status (admin/power user/user)"));
        USER_COMMANDS.add(new CommandObject(false,"/stop", "With this command you can stop the Bot"));
        USER_COMMANDS.add(new CommandObject(false,"/upduser", "Update Username for admins"));
    }
}
