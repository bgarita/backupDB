package Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author bgarita
 */
public class Backup {

    private static final Calendar STARTTIME = GregorianCalendar.getInstance();

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        TimerTask tasknew;
        final long interval = getInterval();
        setStartTime();

        tasknew = new TimerTask() {
            // this method performs the task
            @Override
            public void run() {
                OsaisBackup.main(args);
            } // end run
        };

        // El proceso iniciará si la hora configurada es igual o menor a la
        // hora del sistema.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(tasknew, STARTTIME.getTime(), interval);

    } // end main

    private static long getInterval() throws FileNotFoundException, IOException {
        long interval;
        int min = 60;
        int seg = 60;
        int mil = 1000;

        Properties scheduleProp = new Properties();
        try (FileInputStream fi = new FileInputStream("schedule.properties")) {
            scheduleProp.load(fi);
            fi.close();
        } // end try

        String[] temp = scheduleProp.getProperty("interval").split("-");
        switch (temp[1]) {
            case "H": { // Horas
                interval = Integer.parseInt(temp[0]) * min * seg * mil;
                break;
            }
            case "M": { // Minutos
                interval = Integer.parseInt(temp[0]) * seg * mil;
                break;
            }
            case "S": { // Segundos
                interval = Integer.parseInt(temp[0]) * mil;
                break;
            }
            default: {
                interval = Integer.parseInt(temp[0]);
                break;
            }
        } // end switch

        return interval;
    } // end getInterval

    private static void setStartTime() throws FileNotFoundException, IOException {
        int hour, min;
        Properties scheduleProp = new Properties();
        try (FileInputStream fi = new FileInputStream("schedule.properties")) {
            scheduleProp.load(fi);
            fi.close();
        } // end try

        String[] temp = scheduleProp.getProperty("startTime").split(":");
        hour = Integer.parseInt(temp[0]);
        min = Integer.parseInt(temp[1]);

        STARTTIME.set(Calendar.HOUR_OF_DAY, hour);
        STARTTIME.set(Calendar.MINUTE, min);
        STARTTIME.set(Calendar.SECOND, 0);
        STARTTIME.set(Calendar.MILLISECOND, 0);

    } // end setStartTime
} // end class
