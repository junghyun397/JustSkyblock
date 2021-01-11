package do1phin.mine2021.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerWrapper {

    public static void schedule(Runnable runnable, int delay) {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(timerTask, delay);
    }

}
