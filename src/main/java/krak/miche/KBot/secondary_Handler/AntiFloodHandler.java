package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;

import static krak.miche.KBot.database.SQLUtil.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;


import static krak.miche.KBot.BuildVars.*;


//Not sure if it's the best implementation



/*Come funziona sta roba:
    Ogni ANTIFLOODTIMESLICEMIN il run() aumenta "l'età" dei messaggi inviati di una unità di tempo
    Se l'età supera la soglia di tempo da controllare, il messaggio viene rimosso dalla lista
    A ogni nuovo messaggio inviato, questo viene aggiunto in lista, si fa il conteggio degli effettivi
    messaggi mandati nell'unità di tempo e se supera il limite consentito lo segnala
 */
/**
 * @author Kraktun
 * @version 1.0
 */

public class AntiFloodHandler {


    //Note that time slices don't start when someone set the antiflood, so if you chose 6 sec
    // and in the first 2 seconds someone exceeds the max number of messages, but the bot ends its
    // timeslice(ANTIFLOODTIMESLICEMIN) after the first second, the user is not removed
    public static final String LOGTAG = "ANTIFLOODHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private ArrayList<GroupObject> groups;
    private final ReentrantLock lock = new ReentrantLock();
    private static volatile AntiFloodHandler instance;
    private static boolean isShuttingDown = false;


    private AntiFloodHandler() {
        groups = new ArrayList<>();
        isShuttingDown = false;
    }

    public static AntiFloodHandler getInstance() {
        final AntiFloodHandler currentInstance;
        if (instance == null)
        {
            synchronized (AntiFloodHandler.class) {
                if (instance == null)
                {
                    instance = new AntiFloodHandler();
                }
                currentInstance = instance;
            }
        }
        else
        {
            currentInstance = instance;
        }
        return currentInstance;
    }

    public StringBuilder updateAntiflood(Long group, int messages, int timeSlice) {
        try {
            String language = databaseManager.getGroupLanguage(group);
            databaseManager.updateGroupSettingsAntiflood(group, "true", timeSlice + "", messages + "");
            updateGroup(group);
            return new StringBuilder(Localizer.getString("done", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_update_settings", DEFAULT_LANG));
        }
    }

    public StringBuilder setAntiflood(Long group, String activated) {
        try {
            String language = databaseManager.getGroupLanguage(group);
            if (activated.equals("start"))
            {
                databaseManager.updateGroupSettingsAntiflood(group, "true");
                updateGroup(group);
                return new StringBuilder(Localizer.getString("antiflood_active", language));
            }
            else if (activated.equals("stop"))
            {
                databaseManager.updateGroupSettingsAntiflood(group, "false");
                return new StringBuilder(Localizer.getString("antiflood_stopped", language));
            }
            else
                return new StringBuilder(Localizer.getString("error", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_antiflood", DEFAULT_LANG));
        }
    }

    private void addGroup(Long groupL) {
        String group = longtoString(groupL);
        int[] options = getAntifloodSettings(groupL);
        groups.add(new GroupObject(group, options));
    }

    private int[] getAntifloodSettings(Long groupL) {
        String[] temp = databaseManager.getAntifloodSett(groupL);
        if (temp == null)
            return null;
        int[] ficc = new int[2];
        try {
            ficc[0] = Integer.parseInt(temp[0]);
            ficc[1] = Integer.parseInt(temp[1]);
            return ficc;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateGroup(Long groupL) {
        String group = longtoString(groupL);
        boolean exist = false;
        lock.lock();
        try {
            int[] options = getAntifloodSettings(groupL);
            for (GroupObject greap : groups)
            {
                if (greap.getID().equals(group))
                {
                    greap.setOptions(options);
                    exist = true;
                }
            }
            if (!exist)
                addGroup(groupL);
        } finally {
            lock.unlock();
        }
    }

    //return true if user has sent more messages than those defined in options
    public boolean addUserMessage(Long groupL, int user) {
        String group = longtoString(groupL);
        boolean risposta = false;
        lock.lock();
        try{
            for (GroupObject greap : groups)
            {
                if (greap.getID().equals(group))
                {
                    risposta = greap.addMessage(user);
                }
            }
        }
        finally {
            lock.unlock();
        }
        return risposta;
    }

    public void execute() {
        lock.lock();
        if (!isShuttingDown) {
            try {
                for (GroupObject greap : groups) {
                    greap.increaseMessagesAge();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void interrupt() {
        lock.lock();
        try {
            isShuttingDown = true;
        } finally {
            lock.unlock();
        }
    }

//All important methods are executed inside the lock

    private class GroupObject {
        private String groupID;
        //0 = number of messages
        //1 = timeSlice
        private int[] options;
        private ArrayList<UserObject> users;

        private GroupObject(String group, int[] options) {
            groupID = group;
            this.options = options;
            users = new ArrayList<>();
        }

        int getTimeSlice()
        {
            return options[1];
        }

        String getID() {
            return groupID;
        }

        int getMessages() {
            return options[0];
        }

        void setOptions(int[] options)
        {
            this.options = options;
        }

        //adds a message to the count and
        //return true if message is in the limit of antiflood
        //false if it breaks the rule
        boolean addMessage(int user)
        {
            lock.lock();
            try {
                int messageCount = 1;
                boolean newUser = true;
                for (UserObject uss : users)
                {
                    if (uss.getID() == user)
                    {
                        uss.addMessage();
                        uss.updateMessages(getTimeSlice());
                        messageCount = uss.getMessagesCount();
                        newUser = false;
                        break;
                    }
                }
                if (newUser)
                {
                    users.add(new UserObject(user));
                }
                return messageCount > getMessages();
            } finally {
                lock.unlock();
            }
        }

        //called by run() inside the lock
        void increaseMessagesAge() {
            for (UserObject uss : users)
            {
                uss.updateMessages(options[1]);
                if (uss.getMessagesCount() > 0)
                {
                    uss.increaseAge();
                }
            }
        }
    }

    private class UserObject {
        int user;
        private ArrayList<FloodMessage> messages;
        int messageCount;

        private UserObject(int user) {
            this.user = user;
            messages = new ArrayList<>();
            messageCount = 0;
        }

        private void addMessage() {
            messages.add(new FloodMessage());
            messageCount++;
        }

        private void updateMessages(int timeSlice) {
            if (messageCount > 0)
            {
                for (Iterator<FloodMessage> it = messages.iterator(); it.hasNext(); )
                {
                    FloodMessage mess = it.next();
                    if (mess.isOldMessage(timeSlice))
                    {
                        it.remove();
                        --messageCount;
                    }
                }
            }
        }

        private int getMessagesCount() {
            return messageCount;
        }

        private void increaseAge() {
            if (messageCount > 0)
                for (FloodMessage mess : messages)
                {
                    mess.increaseTime();
                }
        }

        private ArrayList<FloodMessage> getMessages() {
            return messages;
        }

        private int getID() {
            return user;
        }
    }

    private class FloodMessage {

        private int timeSent = 0;

        private FloodMessage() {
            timeSent = 0;
        }

        private void increaseTime() {
            timeSent += ANTIFLOODTIMESLICEMIN;
        }

        private boolean isOldMessage(int timeSlice)
        {
            return timeSent > timeSlice;
        }
    }
}
