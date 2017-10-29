package krak.miche.KBot.services;

import krak.miche.KBot.Jobs.AntifloodJob;
import krak.miche.KBot.Jobs.RemindersJob;
import krak.miche.KBot.secondary_Handler.AntiFloodHandler;
import krak.miche.KBot.secondary_Handler.ReminderHandler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.logging.BotLogger;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static krak.miche.KBot.BuildVars.*;

public class JobExecutor {

    public static final String LOGTAG = "EXECUTORJOB";
    private static volatile JobExecutor instance;
    private final SchedulerFactory schedFact = new StdSchedulerFactory();
    private Scheduler scheduler;
    private static final String ANTIFLOOD_NAME = "Antiflood";
    private static final String REMINDERS_NAME = "Reminders";

    private JobExecutor() {
        try {
            scheduler = schedFact.getScheduler();
        } catch (SchedulerException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    public static JobExecutor getInstance() {
        final JobExecutor currentInstance;
        if (instance == null)
        {
            synchronized (JobExecutor.class) {
                if (instance == null)
                {
                    instance = new JobExecutor();
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

    public void run(){

        try {
            Thread.sleep(THREADS_INIT_DELAY);
            runAntiflood();
            UtilsMain.log("Antiflood started");
            runReminders();
            UtilsMain.log("Reminders started");
            scheduler.start();
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    public boolean shutdown(){
        shutdownAntiflood();
        UtilsMain.log("Antiflood stopped");
        shutdownReminders();
        UtilsMain.log("Reminders stopped");
        try {
            Thread.sleep(TASKSHUTDOWN);
            scheduler.shutdown(true);
            return true;
        } catch (SchedulerException | InterruptedException e) {
            BotLogger.error(LOGTAG, e);
            return false;
        }
    }

    public boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        } catch (SchedulerException e) {
            BotLogger.error(LOGTAG, e);
            return false;
        }
    }

    private void runAntiflood() throws Exception {
        // define the job and tie it to our HelloJob class
        JobDetail jobAntiflood = newJob(AntifloodJob.class)
                .withIdentity(ANTIFLOOD_NAME, "group1")
                .build();

        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = newTrigger()
                .withIdentity("AntifloodTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(ANTIFLOODTIMESLICEMIN)
                        .repeatForever())
                .forJob(jobAntiflood)
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(jobAntiflood, trigger);
    }

    private void runReminders() throws Exception {
        // define the job and tie it to our HelloJob class
        JobDetail jobReminder = newJob(RemindersJob.class)
                .withIdentity(REMINDERS_NAME, "group1")
                .build();

        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = newTrigger()
                .withIdentity("RemindersTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(REMINDERTIMESLICEMIN)
                        .repeatForever())
                .forJob(jobReminder)
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(jobReminder, trigger);
    }

    private void shutdownAntiflood() {
        AntiFloodHandler antiFloodHandler = AntiFloodHandler.getInstance();
        antiFloodHandler.interrupt();
    }

    private void shutdownReminders() {
        ReminderHandler reminderHandler = ReminderHandler.getInstance();
        reminderHandler.interrupt();
    }
}
