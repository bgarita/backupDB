package Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author bgari
 */
public class MyTimerX {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creating timer task, timer
        TimerTask tasknew;
        tasknew = new TimerTask() {
            // this method performs the task
            @Override
            public void run() {
                System.out.println("working at fixed rate");
            } // end run
        };
        Timer timer = new Timer();

        // scheduling the task at fixed rate
        timer.scheduleAtFixedRate(tasknew,new Date(),1000);
    } // end main
} // end class
